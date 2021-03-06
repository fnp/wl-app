package com.folioreader.view;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.folioreader.Config;
import com.folioreader.Constants;
import com.folioreader.R;
import com.folioreader.model.event.BusOwner;
import com.folioreader.model.event.ReloadDataEvent;
import com.folioreader.ui.folio.activity.ToolbarUtils;
import com.folioreader.util.AppUtil;
import com.folioreader.util.UiUtil;


/**
 * Created by mobisys2 on 11/16/2016.
 */

public class ConfigBottomSheetDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    public static final int DAY_BUTTON = 30;
    public static final int NIGHT_BUTTON = 31;
    private static final int FADE_DAY_NIGHT_MODE = 500;

    private CoordinatorLayout.Behavior mBehavior;
    private boolean mIsNightMode = false;


    private RelativeLayout mContainer;
    private ImageView mDayButton;
    private ImageView mNightButton;
    private SeekBar mFontSizeSeekBar;
    private SeekBar mMarginSizeSeekBar;
    private SeekBar mInterlineSizeSeekBar;
    private View mDialogView;
    private ConfigDialogCallback mConfigDialogCallback;
    private Config mConfig;

    public interface ConfigDialogCallback {
        void onOrientationChange(int orentation);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_config, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                FrameLayout bottomSheet = (FrameLayout)
                        dialog.findViewById(android.support.design.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(0);
            }
        });

        mDialogView = view;
        mConfig = AppUtil.getSavedConfig(getActivity());
        initViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDialogView.getViewTreeObserver().addOnGlobalLayoutListener(null);
    }

    private void initViews() {
        inflateView();
        configFonts();
        mFontSizeSeekBar.setProgress(mConfig.getFontSize());
        mMarginSizeSeekBar.setProgress(mConfig.getMarginSize());
        mInterlineSizeSeekBar.setProgress(mConfig.getInterlineSize());
        configSeekBars();
        selectFont(mConfig.getFont(), false);
        mIsNightMode = mConfig.isNightMode();

        if (mIsNightMode) {
            mContainer.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.night));
            mDayButton.setSelected(false);
            mNightButton.setSelected(true);
            UiUtil.setColorToImage(getActivity(), mConfig.getThemeColor(), mNightButton.getDrawable());
            UiUtil.setColorToImage(getActivity(), R.color.app_gray, mDayButton.getDrawable());
        } else {
            mContainer.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
            mDayButton.setSelected(true);
            mNightButton.setSelected(false);
            UiUtil.setColorToImage(getActivity(), mConfig.getThemeColor(), mDayButton.getDrawable());
            UiUtil.setColorToImage(getActivity(), R.color.app_gray, mNightButton.getDrawable());
        }

        mConfigDialogCallback = (ConfigDialogCallback) getActivity();
    }

    private void inflateView() {
        mContainer = (RelativeLayout) mDialogView.findViewById(R.id.container);
        mFontSizeSeekBar = (SeekBar) mDialogView.findViewById(R.id.seekbar_font_size);
	    mMarginSizeSeekBar = mDialogView.findViewById(R.id.seekbar_margin_size);
	    mInterlineSizeSeekBar = mDialogView.findViewById(R.id.seekbar_interline_size);
	    mDayButton = (ImageView) mDialogView.findViewById(R.id.day_button);
        mNightButton = (ImageView) mDialogView.findViewById(R.id.night_button);
        mDayButton.setTag(DAY_BUTTON);
        mNightButton.setTag(NIGHT_BUTTON);
        mDayButton.setOnClickListener(this);
        mNightButton.setOnClickListener(this);
    }


    private void configFonts() {
        ((StyleableTextView) mDialogView.findViewById(R.id.btn_font_ebgaramond)).setTextColor(UiUtil.getColorList(getActivity(), mConfig.getThemeColor(), R.color.grey_color));
        ((StyleableTextView) mDialogView.findViewById(R.id.btn_font_lato)).setTextColor(UiUtil.getColorList(getActivity(), mConfig.getThemeColor(), R.color.grey_color));
        ((StyleableTextView) mDialogView.findViewById(R.id.btn_font_lora)).setTextColor(UiUtil.getColorList(getActivity(), mConfig.getThemeColor(), R.color.grey_color));
        ((StyleableTextView) mDialogView.findViewById(R.id.btn_font_raleway)).setTextColor(UiUtil.getColorList(getActivity(), mConfig.getThemeColor(), R.color.grey_color));
        mDialogView.findViewById(R.id.btn_font_ebgaramond).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFont(Constants.FONT_EBGARAMOND, true);
            }
        });

        mDialogView.findViewById(R.id.btn_font_lato).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFont(Constants.FONT_LATO, true);
            }
        });

        mDialogView.findViewById(R.id.btn_font_lora).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFont(Constants.FONT_LORA, true);
            }
        });

        mDialogView.findViewById(R.id.btn_font_raleway).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFont(Constants.FONT_RALEWAY, true);
            }
        });
    }

    private void selectFont(int selectedFont, boolean isReloadNeeded) {
        if (selectedFont == Constants.FONT_EBGARAMOND) {
            mDialogView.findViewById(R.id.btn_font_ebgaramond).setSelected(true);
            mDialogView.findViewById(R.id.btn_font_lato).setSelected(false);
            mDialogView.findViewById(R.id.btn_font_lora).setSelected(false);
            mDialogView.findViewById(R.id.btn_font_raleway).setSelected(false);
        } else if (selectedFont == Constants.FONT_LATO) {
            mDialogView.findViewById(R.id.btn_font_ebgaramond).setSelected(false);
            mDialogView.findViewById(R.id.btn_font_lato).setSelected(true);
            mDialogView.findViewById(R.id.btn_font_lora).setSelected(false);
            mDialogView.findViewById(R.id.btn_font_raleway).setSelected(false);
        } else if (selectedFont == Constants.FONT_LORA) {
            mDialogView.findViewById(R.id.btn_font_ebgaramond).setSelected(false);
            mDialogView.findViewById(R.id.btn_font_lato).setSelected(false);
            mDialogView.findViewById(R.id.btn_font_lora).setSelected(true);
            mDialogView.findViewById(R.id.btn_font_raleway).setSelected(false);
        } else if (selectedFont == Constants.FONT_RALEWAY) {
            mDialogView.findViewById(R.id.btn_font_ebgaramond).setSelected(false);
            mDialogView.findViewById(R.id.btn_font_lato).setSelected(false);
            mDialogView.findViewById(R.id.btn_font_lora).setSelected(false);
            mDialogView.findViewById(R.id.btn_font_raleway).setSelected(true);
        }

        mConfig.setFont(selectedFont);
        //if (mConfigDialogCallback != null) mConfigDialogCallback.onConfigChange();
        if (isAdded() && isReloadNeeded) {
            AppUtil.saveConfig(getActivity(),mConfig);

            Activity activity = getActivity();
            if (activity instanceof BusOwner)
                ((BusOwner) activity).getBus().post(new ReloadDataEvent());
        }
    }

    private void toggleBlackTheme() {

        int day = getResources().getColor(R.color.white);
        int night = getResources().getColor(R.color.night);
        int darkNight = getResources().getColor(R.color.dark_night);
        final int diffNightDark = night - darkNight;

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(),
                mIsNightMode ? day : night, mIsNightMode ? night : day);
        colorAnimation.setDuration(FADE_DAY_NIGHT_MODE);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int value = (int) animator.getAnimatedValue();
                mContainer.setBackgroundColor(value);
            }
        });

        colorAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
               // mIsNightMode = !mIsNightMode;
                mConfig.setNightMode(mIsNightMode);
                AppUtil.saveConfig(getActivity(),mConfig);

                Activity activity = getActivity();
                if (activity instanceof BusOwner)
                    ((BusOwner) activity).getBus().post(new ReloadDataEvent());

                ///mConfigDialogCallback.onConfigChange();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        colorAnimation.setDuration(FADE_DAY_NIGHT_MODE);
        colorAnimation.start();
    }

    private void configSeekBars() {
	    setupSeekBarThumb(mFontSizeSeekBar);
	    setupSeekBarThumb(mMarginSizeSeekBar);
	    setupSeekBarThumb(mInterlineSizeSeekBar);

        mFontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mConfig.setFontSize(progress);
                saveConfigAndNotifyBusOwner();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        mMarginSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	        @Override
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		        mConfig.setMarginSize(progress);
		        saveConfigAndNotifyBusOwner();
	        }

	        @Override
	        public void onStartTrackingTouch(SeekBar seekBar) {
	        }

	        @Override
	        public void onStopTrackingTouch(SeekBar seekBar) {
	        }
        });
        mInterlineSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
	        @Override
	        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		        mConfig.setInterlineSize(progress);
		        saveConfigAndNotifyBusOwner();
	        }

	        @Override
	        public void onStartTrackingTouch(SeekBar seekBar) {
	        }

	        @Override
	        public void onStopTrackingTouch(SeekBar seekBar) {
	        }
        });
    }

	private void setupSeekBarThumb(SeekBar seekBar) {
		Drawable thumbDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.seekbar_thumb);
		UiUtil.setColorToImage(getActivity(), mConfig.getThemeColor(), (thumbDrawable));
		UiUtil.setColorToImage(getActivity(), mConfig.getThemeColor(), seekBar.getProgressDrawable());
		seekBar.setThumb(thumbDrawable);
	}

	private void saveConfigAndNotifyBusOwner() {
	    AppUtil.saveConfig(getActivity(),mConfig);
	    Activity activity = getActivity();
	    if (activity instanceof BusOwner) {
		    ((BusOwner) activity).getBus().post(new ReloadDataEvent());
	    }
    }


    @Override
    public void onClick(View v) {
        switch (((Integer) v.getTag())) {
            case DAY_BUTTON:
                mIsNightMode = false;
                toggleBlackTheme();
                mDayButton.setSelected(true);
                mNightButton.setSelected(false);
                setToolBarColor();
                setAudioPlayerBackground();
                UiUtil.setColorToImage(getActivity(), R.color.app_gray, mNightButton.getDrawable());
                UiUtil.setColorToImage(getActivity(), mConfig.getThemeColor(), mDayButton.getDrawable());
                break;
            case NIGHT_BUTTON:
                mIsNightMode = true;
                toggleBlackTheme();
                mDayButton.setSelected(false);
                mNightButton.setSelected(true);
                UiUtil.setColorToImage(getActivity(), mConfig.getThemeColor(), mNightButton.getDrawable());
                UiUtil.setColorToImage(getActivity(), R.color.app_gray, mDayButton.getDrawable());
                setToolBarColor();
                setAudioPlayerBackground();
                break;
            default:
                break;
        }
    }

    private void setToolBarColor() {
        Toolbar toolbar = ((Activity) getContext()).findViewById(R.id.toolbar);
        ToolbarUtils.updateToolbarColors(getContext(), toolbar, mConfig, mIsNightMode);
    }

    private void setAudioPlayerBackground() {
        if (mIsNightMode) {
            ((Activity) getContext()).
                    findViewById(R.id.container).
                    setBackgroundColor(ContextCompat.getColor(getContext(), R.color.night));
        } else {
            ((Activity) getContext()).
                    findViewById(R.id.container).
                    setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        }
    }
}
