package com.example.kimsuhong.lifenote;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class RecordManager implements MediaPlayer.OnCompletionListener
{
    public static final String SAVE_PATH = "/LifeNote/RecordAudio";
    public boolean isRecordSuccess = false;
    public static final int REC_STOP = 0;
    public static final int RECORDING = 1;
    public static final int PLAY_STOP = 0;
    public static final int PLAYING = 1;
    public static final int PLAY_PAUSE = 2;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    public int mRecState = REC_STOP;
    public int mPlayerState = PLAY_STOP;
    private SeekBar mRecProgressBar, mPlayProgressBar;
    private Button mBtnStartRec, mBtnStartPlay;
    private String mFilePath, mFileName;
    private TextView mTvPlayMaxPoint;

    private int mCurRecTimeMs = 0;
    private int mCurProgressTimeDisplay = 0;
    public RecordManager(SeekBar playProgressBar,Button btnPlay, TextView maxPointText){ // 재생을 위한
        mFilePath = SunUtil.makeDir(SAVE_PATH);
        mPlayProgressBar = playProgressBar; mBtnStartPlay = btnPlay; mTvPlayMaxPoint = maxPointText;
    }
    public RecordManager(SeekBar recProgressBar, SeekBar playProgressBar,Button btnRec,Button btnPlay, TextView maxPointText){ // 재생, 녹화 등 풀버전
        mFilePath = SunUtil.makeDir(SAVE_PATH);
        mRecProgressBar = recProgressBar; mPlayProgressBar = playProgressBar; mBtnStartRec = btnRec; mBtnStartPlay = btnPlay; mTvPlayMaxPoint = maxPointText;
    }

    public String getFilePath(){
        return mFilePath + mFileName;
    }
    public void setFileName(String name){
        mFileName = name;
    }
    public void mBtnRecOnClick() {
        if (mRecState == REC_STOP) {
            mRecState = RECORDING;
            startRec();
            updateUI();
        } else if (mRecState == RECORDING) {
            mRecState = REC_STOP;
            stopRec();
            updateUI();
        }
    }
    // 녹음시작
    public void startRec() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis()));
        mFileName = "record"+timeStamp+".amr";
        mCurRecTimeMs = 0;
        mCurProgressTimeDisplay = 0;
        isRecordSuccess = false;

        // SeekBar의 상태를 0.1초후 체크 시작
        mProgressHandler.sendEmptyMessageDelayed(0, 100);

        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.reset();
        } else {
            mRecorder.reset();
        }

        try {
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mRecorder.setOutputFile(mFilePath + mFileName);
            mRecorder.prepare();
            mRecorder.start();
        } catch (IllegalStateException e) {
            Log.e("record","IllegalStateException");
        } catch (IOException e) {
            Log.e("record","IOException");
        }
    }
    // 녹음정지
    public void stopRec() {
        try {
            mRecorder.stop();
            isRecordSuccess = true;
        } catch (Exception e) {
        } finally {
            mRecorder.release();
            mRecorder = null;
        }

        mCurRecTimeMs = -999;
        // SeekBar의 상태를 즉시 체크
        mProgressHandler.sendEmptyMessageDelayed(0, 0);
    }

    public void mBtnPlayOnClick() {
        if (mPlayerState == PLAY_STOP) {
            mPlayerState = PLAYING;
            startPlay();
            updateUI();
        } else if (mPlayerState == PLAYING) {
            mPlayerState = PLAY_STOP;
            stopPlay();
            updateUI();
        }
    }
    // 재생 시작
    private void startPlay() {
        // 미디어 플레이어 생성
        if (mPlayer == null)
            mPlayer = new MediaPlayer();
        else
            mPlayer.reset();

        mPlayer.setOnCompletionListener(this);

        String fullFilePath = mFilePath + mFileName;
        Log.v("ProgressRecorder", "녹음파일명 ==========> " + fullFilePath);

        try {
            mPlayer.setDataSource(fullFilePath);
            mPlayer.prepare();
            int point = mPlayer.getDuration();
            mPlayProgressBar.setMax(point);

            int maxMinPoint = point / 1000 / 60;
            int maxSecPoint = (point / 1000) % 60;
            String maxMinPointStr;
            String maxSecPointStr;

            if (maxMinPoint < 10)
                maxMinPointStr = "0" + maxMinPoint + ":";
            else
                maxMinPointStr = maxMinPoint + ":";

            if (maxSecPoint < 10)
                maxSecPointStr = "0" + maxSecPoint;
            else
                maxSecPointStr = String.valueOf(maxSecPoint);

            mTvPlayMaxPoint.setText(maxMinPointStr + maxSecPointStr);
        } catch (Exception e) {
            Log.v("ProgressRecorder", "미디어 플레이어 Prepare Error ==========> " + e);
        }

        if (mPlayerState == PLAYING) {
            mPlayProgressBar.setProgress(0);

            try {
                // SeekBar의 상태를 0.1초마다 체크
                mProgressHandler2.sendEmptyMessageDelayed(0, 100);
                mPlayer.start();
            } catch (Exception e) {
                Log.e("record","error: "+e.getMessage());
            }
        }
    }

    private void stopPlay() {
        // 재생을 중지하고
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        mPlayProgressBar.setProgress(0);

        // 즉시 SeekBar 메세지 핸들러를 호출한다.
        mProgressHandler2.sendEmptyMessageDelayed(0, 0);
    }
    private void updateUI() {
        if(mRecProgressBar != null) {
            if (mRecState == REC_STOP) {
                mBtnStartRec.setText("녹음 시작");
                mRecProgressBar.setProgress(0);
            } else if (mRecState == RECORDING)
                mBtnStartRec.setText("녹음 중지");
        }
        if (mPlayerState == PLAY_STOP) {
            mBtnStartPlay.setText("재생해보기");
            mPlayProgressBar.setProgress(0);
        } else if (mPlayerState == PLAYING)
            mBtnStartPlay.setText("재생 중지");
    }

    public void onCompletion(MediaPlayer mp) {
        mPlayerState = PLAY_STOP; // 재생이 종료됨

        // 재생이 종료되면 즉시 SeekBar 메세지 핸들러를 호출한다.
        mProgressHandler2.sendEmptyMessageDelayed(0, 0);
    }

    // 녹음시 SeekBar처리
    Handler mProgressHandler = new Handler() {
        public void handleMessage(Message msg) {
            mCurRecTimeMs = mCurRecTimeMs + 100;
            mCurProgressTimeDisplay = mCurProgressTimeDisplay + 100;

            // 녹음시간이 음수이면 정지버튼을 눌러 정지시켰음을 의미하므로
            // SeekBar는 그대로 정지시키고 레코더를 정지시킨다.
            if (mCurRecTimeMs < 0) {
            }
            // 녹음시간이 아직 최대녹음제한시간보다 작으면 녹음중이라는 의미이므로
            // SeekBar의 위치를 옮겨주고 0.1초 후에 다시 체크하도록 한다.
            else if (mCurRecTimeMs < 60000) {
                mRecProgressBar.setProgress(mCurProgressTimeDisplay);
                mProgressHandler.sendEmptyMessageDelayed(0, 100);
            }
            // 녹음시간이 최대 녹음제한 시간보다 크면 녹음을 정지 시킨다.
            else {
                mBtnRecOnClick();
            }
        }
    };

    // 재생시 SeekBar 처리
    Handler mProgressHandler2 = new Handler() {
        public void handleMessage(Message msg) {
            if (mPlayer == null)
                return;

            try {
                if (mPlayer.isPlaying()) {
                    mPlayProgressBar.setProgress(mPlayer.getCurrentPosition());
                    mProgressHandler2.sendEmptyMessageDelayed(0, 100);
                } else {
                    mPlayer.release();
                    mPlayer = null;

                    updateUI();
                }
            } catch (IllegalStateException e) {
            } catch (Exception e) {
            }
        }
    };
    public static class SunUtil {
        // 디렉토리를 만든다.
        public static String makeDir(String dirName) {
            String mRootPath = Environment.getExternalStorageDirectory() + dirName;

            try {
                File fRoot = new File(mRootPath);
                if (fRoot.exists() == false) {
                    if (fRoot.mkdirs() == false) {
                        throw new Exception("");
                    }
                }
            } catch (Exception e) {
                mRootPath = "-1";
            }

            return mRootPath + "/";
        }
    }
}
