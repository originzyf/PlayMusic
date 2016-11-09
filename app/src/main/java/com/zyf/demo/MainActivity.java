package com.zyf.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.zyf.demo.Player.OnStatueChangedListener;

public class MainActivity extends Activity {

	private Player				player;
	private SeekBar				musicProgress;
	/** 快进 */
	private ImageButton				mSpeed;
	/** 快退 */
	private ImageButton				mRewind;

	private ImageButton mMusic_play;

	private int					progress;

	public static final int		STATUE_INIT		= 1;

	public static final int		STATUE_PREPARE	= 2;

	public static final int		STATUE_PLAY		= 3;

	public static final int		STATUE_PAUSE	= 4;

	public int					statue			= 1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ButtonClickListener listener = new ButtonClickListener();
		mSpeed = (ImageButton) findViewById(R.id.speed);
		mRewind = (ImageButton) findViewById(R.id.rewind);
		mMusic_play = (ImageButton) findViewById(R.id.music_play);
		mMusic_play.setOnClickListener(listener);
		musicProgress = (SeekBar) findViewById(R.id.music_progress);
		player = new Player(musicProgress, new OnStatueChangedListener() {

			@Override
			public void onStart() {
				// 开始播放
				mMusic_play.setBackgroundResource(R.drawable.player_pause);
				statue = STATUE_PAUSE;
			}

			@Override
			public void onPrepare() {
				// 初始化准备播放
				statue = STATUE_PREPARE;
			}

			@Override
			public void onPause() {
				// 暂停
				mMusic_play.setBackgroundResource(R.drawable.player_play);
				statue = STATUE_PLAY;
			}

			@Override
			public void onFinish() {
				// 播放完成
				mMusic_play.setBackgroundResource(R.drawable.player_play);
				statue = STATUE_INIT;
			}

		});
		musicProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());



		mSpeed.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				progress += player.mediaPlayer.getDuration() / 15;
				if (progress > player.mediaPlayer.getDuration()) {
					progress = player.mediaPlayer.getDuration();
				}
				player.mediaPlayer.seekTo(progress);
			}
		});

		mRewind.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				progress -= player.mediaPlayer.getDuration() / 15;
				if (progress < 0) {
					progress = 0;
				}
				player.mediaPlayer.seekTo(progress);
			}
		});
	}

	private final class ButtonClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.music_play:
					switch (statue) {
						case STATUE_INIT:
							new Thread(new Runnable()
							{

								@Override
								public void run()
								{
									player.playUrl("http://abv.cn/music/光辉岁月.mp3");
								}
							}).start();
							break;

						case STATUE_PLAY:
							// 调用播放的逻辑
							player.play();
							break;
						case STATUE_PAUSE:
							// 调用暂停的逻辑
							player.pause();
							break;
						case STATUE_PREPARE:
							// 暂时没用
							break;

						default:
							break;
					}
					break;
			}
		}
	}

	class SeekBarChangeEvent implements OnSeekBarChangeListener {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
			MainActivity.this.progress = progress * player.mediaPlayer.getDuration() / seekBar.getMax();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
			player.mediaPlayer.seekTo(progress);
		}

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (player != null) {
			player.stop();
			player = null;
		}
	}

}
