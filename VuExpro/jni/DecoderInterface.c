

#ifdef __cplusplus
extern "C" {
#endif


#include <jni.h>

#include <string.h>
#include <stdio.h>
#include <stdlib.h>


#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>

#include <android/bitmap.h>


#include <android/log.h>

#define  LOG_TAG    "FFMPEGSample"
//#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print( ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__ )

#define COLOR_FORMAT PIX_FMT_RGB32

static void fill_bitmap( AndroidBitmapInfo* info, void *pixels, AVFrame *pFrame )
{
	uint8_t *frameLine;
	int yy = 0;
	int xx = 0;

	for( yy = 0; yy < info->height; yy++ )
	{
		uint8_t*  line = (uint8_t*)pixels;
		frameLine = (uint8_t *)pFrame->data[0] + ( yy * pFrame->linesize[0] );

		for( xx = 0; xx < info->width; xx++ )
		{
			int out_offset = xx * 4;
			int in_offset = xx * 3;
			line[ out_offset ] = frameLine[ in_offset ];
			line[ out_offset + 1 ] = frameLine[ in_offset + 1 ];
			line[ out_offset + 2 ] = frameLine[ in_offset + 2 ];
			line[ out_offset + 3 ] = 0;
		}
		pixels = (char*)pixels + info->stride;
	}
}

typedef struct
{
	AVFormatContext *pFormatCtx;
	AVCodecContext 	*pCodecCtx;
	AVFrame 		*pFrame;
	AVFrame 		*pFrameRGB;
	AVCodec			*pAvcodec;
	void			*buffer;
}VideoDecodeContext;


JNIEXPORT jint JNICALL Java_com_example_test_H264StreamSource_H264DecoderOpen( JNIEnv * env, jobject this )
{
	//avcodec_init();
	av_register_all();

	VideoDecodeContext *pContext = calloc( 1, sizeof(VideoDecodeContext));

	pContext->pFormatCtx = NULL;;
	pContext->pCodecCtx = NULL;
	pContext->pFrame = NULL;;
	pContext->pFrameRGB = NULL;
	pContext->pAvcodec = NULL;;
	pContext->buffer = NULL;

	pContext->pAvcodec = avcodec_find_decoder( CODEC_ID_H264 );
	pContext->pCodecCtx = avcodec_alloc_context3( pContext->pAvcodec );
	pContext->pCodecCtx->pix_fmt = COLOR_FORMAT;
	pContext->pFrame = avcodec_alloc_frame();
	int nRet = avcodec_open2( pContext->pCodecCtx, pContext->pAvcodec, 0 );

	if( nRet == 0 )
		return (jint)(int)pContext;
	else
	{
		if( pContext )
		{
			if( pContext->buffer )
				free( pContext->buffer );

			if( pContext->pCodecCtx )
				avcodec_close( pContext->pCodecCtx );
			free( pContext );
		}
	}

	return 0;
}

JNIEXPORT jint JNICALL Java_com_example_test_H264StreamSource_H264DecoderClose( JNIEnv * env, jobject this, jint myDecoderObj )
{
	VideoDecodeContext *pContext = (VideoDecodeContext*)myDecoderObj;

	if( pContext )
	{
		if( pContext->buffer )
			free( pContext->buffer );

		if( pContext->pCodecCtx )
			avcodec_close( pContext->pCodecCtx );

		free( pContext );
	}

}

JNIEXPORT void JNICALL Java_com_example_test_H264StreamSource_H264DecoderDecodeFrame( JNIEnv * env, jobject this, jint myDecoderObj, jbyteArray VideoDataArray, jint VideoDataArraySize, jstring bitmap )
{

	AndroidBitmapInfo  info;
	void*              pixels;
	int                ret;
	int err;
	int i;
	int frameFinished = 0;
	AVPacket packet;
	static struct SwsContext *img_convert_ctx;
	int64_t seek_target;

	int nObjAddr = myDecoderObj;

	VideoDecodeContext *pDecoderObj = (VideoDecodeContext*)nObjAddr;

	if( ( ret = AndroidBitmap_getInfo( env, bitmap, &info ) ) < 0 )
	{
		//LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
		return;
	}

	//LOGE( "AndroidBitmap_getInfo OK " );

	if( ( ret = AndroidBitmap_lockPixels( env, bitmap, &pixels ) ) < 0 )
	{
		//LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
	}

	//LOGE( "AndroidBitmap_lockPixels OK " );

//	jobject p1e;
//	p1e = (*env)->GetObjectArrayElement( env, VideoDataArray, 2 );

	jbyte *b = (jbyte *)(*env)->GetByteArrayElements( env, VideoDataArray, NULL );


	packet.data = b;//(char *) (*env)->GetByteArrayElements( env, p1e, NULL );
	packet.size = VideoDataArraySize;

	av_init_packet( &packet );
/*
	LOGE( "Decode : %d-%d-%d-%d-%d", pDecoderObj, pDecoderObj->pCodecCtx,  pDecoderObj->pFrame, b, VideoDataArraySize );

	LOGE( "Decode : %02X %02X %02X %02X  %02X %02X %02X %02X",
			packet.data[0],
			packet.data[1],
			packet.data[2],
			packet.data[3],
			packet.data[4],
			packet.data[5],
			packet.data[6],
			packet.data[7] );
*/
	avcodec_decode_video2( pDecoderObj->pCodecCtx, pDecoderObj->pFrame, &frameFinished, &packet );

	//LOGE( "Decode OK " );

	if( frameFinished == 0 )
		avcodec_decode_video2( pDecoderObj->pCodecCtx, pDecoderObj->pFrame, &frameFinished, &packet );


	//(*env)->ReleaseByteArrayElements( env, p1e, (char *) packet.data, 0);


	if( frameFinished )
	{
		//LOGE("packet pts %llu", packet.pts);
		// This is much different than the tutorial, sws_scale
		// replaces img_convert, but it's not a complete drop in.
		// This version keeps the image the same size but swaps to
		// RGB24 format, which works perfect for PPM output.

		//int target_width = 320;
		//int target_height = 240;
		img_convert_ctx = sws_getContext( 	pDecoderObj->pCodecCtx->width,
											pDecoderObj->pCodecCtx->height,
											pDecoderObj->pCodecCtx->pix_fmt,

											// target w x h
											pDecoderObj->pCodecCtx->width,
											pDecoderObj->pCodecCtx->height,
											// target format
											COLOR_FORMAT,


											SWS_BICUBIC,
											NULL, NULL, NULL);

		if( img_convert_ctx == NULL )
		{
			//LOGE("could not initialize conversion context\n");
			return;
		}

		if( pDecoderObj->buffer == NULL && pDecoderObj->pFrameRGB == NULL )
		{
			pDecoderObj->pFrameRGB = avcodec_alloc_frame();
			//LOGI("Video size is [%d x %d]", pCodecCtx->width, pCodecCtx->height);
			int numBytes = avpicture_get_size( COLOR_FORMAT, pDecoderObj->pCodecCtx->width, pDecoderObj->pCodecCtx->height );
			pDecoderObj->buffer = (uint8_t *)av_malloc( numBytes * sizeof( uint8_t ) );
			avpicture_fill( (AVPicture *)pDecoderObj->pFrameRGB, pDecoderObj->buffer, COLOR_FORMAT, pDecoderObj->pCodecCtx->width, pDecoderObj->pCodecCtx->height );
		}


		sws_scale( 	img_convert_ctx,
					(const uint8_t* const*)pDecoderObj->pFrame->data,
					pDecoderObj->pFrame->linesize,
					0,
					pDecoderObj->pCodecCtx->height,
					pDecoderObj->pFrameRGB->data,
					pDecoderObj->pFrameRGB->linesize );

		memcpy( (uint8_t*)pixels, (uint8_t *)pDecoderObj->pFrameRGB->data[0], info.stride * info.height );
		//fill_bitmap( &info, pixels, pDecoderObj->pFrameRGB );
	}

	av_free_packet( &packet );
	(*env)->ReleaseByteArrayElements( env, VideoDataArray, b, 0 );

	AndroidBitmap_unlockPixels( env, bitmap );

}

//JNIEXPORT void JNICALL Java_com_example_test_MainActivity_printHello( JNIEnv * env, jobject jobj )

JNIEXPORT jstring JNICALL Java_com_example_test_H264StreamSource_printHello( JNIEnv * env, jobject thiz )
{
    char str[256];
    sprintf(str, "%d\0", avcodec_version() );
    return (*env)->NewStringUTF(env, str);
}



#ifdef __cplusplus
}
#endif
