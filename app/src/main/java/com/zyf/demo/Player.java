/*
 * 
 */
package com.zyf.demo;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;
import android.widget.SeekBar;

/**
 * The Class Player.
 */
public class Player implements OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener
{

	/** The media player. */
	public MediaPlayer				mediaPlayer;			// 媒体播放器

	/** The seek bar. */
	private SeekBar					seekBar;				// 拖动条

	/** The Timer. */
	private Timer					mTimer	= new Timer();	// 计时器

	private OnStatueChangedListener	mChangedListener;

	/**
	 * Instantiates a new player.
	 * 
	 * @param seekBar the seek bar
	 */
	// 初始化播放器
	public Player(SeekBar seekBar, OnStatueChangedListener onStatusChangedListener)
	{
		super();
		this.mChangedListener = onStatusChangedListener;
		this.seekBar = seekBar;
		try
		{
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnCompletionListener(this);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		// 每一秒触发一次
		mTimer.schedule(timerTask, 0, 1000);
	}

	/** The timer task. */
	// 计时器
	TimerTask	timerTask	= new TimerTask()
							{

								@Override
								public void run()
								{
									if (mediaPlayer == null)
										return;
									if (mediaPlayer.isPlaying() && seekBar.isPressed() == false)
									{
										handler.sendEmptyMessage(0); // 发送消息
									}
								}
							};

	/** The handler. */
	Handler		handler		= new Handler()
							{
								public void handleMessage(android.os.Message msg)
								{
									if (mediaPlayer != null)
									{
										int position = mediaPlayer.getCurrentPosition();
										int duration = mediaPlayer.getDuration();
										if (duration > 0)
										{
											// 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
											long pos = seekBar.getMax() * position / duration;
											seekBar.setProgress((int) pos);
										}
									}

								};
							};

	/**
	 * Play.
	 */
	public void play()
	{
		mediaPlayer.start();
		if (mChangedListener != null)
		{
			mChangedListener.onStart();
		}
	}

	/**
	 * Play url.
	 * 
	 * @param url url地址
	 */
	public void playUrl(String url)
	{
		try
		{
			mediaPlayer.reset();
			mediaPlayer.setDataSource(url); // 设置数据源
			mediaPlayer.prepare(); // prepare自动播放

			if (mChangedListener != null)
			{
				mChangedListener.onPrepare();
			}
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (IllegalStateException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Pause.
	 */
	// 暂停
	public void pause()
	{
		mediaPlayer.pause();
		if (mChangedListener != null)
		{
			mChangedListener.onPause();
		}
	}

	/**
	 * Stop.
	 */
	// 停止
	public void stop()
	{
		if (mediaPlayer != null)
		{
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	/* （非 Javadoc）
	 * @see android.media.MediaPlayer.OnPreparedListener#onPrepared(android.media.MediaPlayer)
	 */
	@Override
	public void onPrepared(MediaPlayer mp)
	{
		mp.start();
		if (mChangedListener != null)
		{
			mChangedListener.onStart();
		}
		Log.e("123", "准备---------------------------------");
	}

	/* （非 Javadoc）
	 * @see android.media.MediaPlayer.OnCompletionListener#onCompletion(android.media.MediaPlayer)
	 */
	@Override
	public void onCompletion(MediaPlayer mp)
	{
		if (mChangedListener != null)
		{
			mChangedListener.onFinish();
		}
		Log.e("123", "完成---------------------------------");
	}

	/**
	 * 缓冲更新.
	 * 
	 * @param mp the mp
	 * @param percent the percent
	 */
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent)
	{
		seekBar.setSecondaryProgress(percent);
		int currentProgress = seekBar.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
		Log.e(currentProgress + "% play", percent + " buffer");
	}

	/**
	 * The listener interface for receiving onStatusChanged events.
	 * The class that is interested in processing a onStatusChanged
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addonStatusChangedListener<code> method. When
	 * the onStatusChanged event occurs, that object's appropriate
	 * method is invoked.
	 *
	 */
	public interface OnStatueChangedListener
	{

		/**
		 * 完成的回调.
		 */
		void onFinish();

		/**
		 * 初始化时的回调.
		 */
		void onPrepare();

		/**
		 * 开始播放的回调.
		 */
		void onStart();

		/**
		 * 暂停的回调
		 */
		void onPause();
	}

}
