package com.folioreader.ui.folio.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.folioreader.Config;
import com.folioreader.Constants;
import com.folioreader.R;
import com.folioreader.ui.folio.fragment.HighlightFragment;
import com.folioreader.ui.tableofcontents.view.TableOfContentFragment;
import com.folioreader.util.AppUtil;
import com.folioreader.util.FolioReader;
import com.folioreader.util.UiUtil;

public class ContentHighlightActivity extends AppCompatActivity {
    private boolean mIsNightMode;
    private Config mConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_highlight);
        getSupportActionBar().hide();
        mConfig = AppUtil.getSavedConfig(this);
        mIsNightMode = mConfig.isNightMode();
        initViews();
    }

    private void initViews() {
        findViewById(R.id.layout_content_highlights).setVisibility(View.GONE);
        // findViewById(R.id.layout_content_highlights).setBackgroundDrawable(UiUtil.getShapeDrawable(this, mConfig.getThemeColor()));
        if (mIsNightMode) {
            findViewById(R.id.toolbar).setBackgroundColor(Color.BLACK);
            UiUtil.setColorToImage(this, mConfig.getThemeColor(), ((ImageView) findViewById(R.id.btn_close)).getDrawable());
            ((TextView) findViewById(R.id.tvTitle)).setTextColor(UiUtil.getColorList(this,mConfig.getThemeColor(), mConfig.getThemeColor()));

            findViewById(R.id.btn_contents).setBackgroundDrawable(UiUtil.convertColorIntoStateDrawable(this, mConfig.getThemeColor(), R.color.black));
            findViewById(R.id.btn_highlights).setBackgroundDrawable(UiUtil.convertColorIntoStateDrawable(this, mConfig.getThemeColor(), R.color.black));
            ((TextView) findViewById(R.id.btn_contents)).setTextColor(UiUtil.getColorList(this, R.color.black, mConfig.getThemeColor()));
            ((TextView) findViewById(R.id.btn_highlights)).setTextColor(UiUtil.getColorList(this, R.color.black, mConfig.getThemeColor()));

        } else {
            findViewById(R.id.toolbar).setBackgroundColor(ContextCompat.getColor(this, mConfig.getToolbarColor()));
            UiUtil.setColorToImage(this, mConfig.getIconColor(), ((ImageView) findViewById(R.id.btn_close)).getDrawable());
            ((TextView) findViewById(R.id.tvTitle)).setTextColor(UiUtil.getColorList(this,mConfig.getIconColor(), mConfig.getIconColor()));

            ((TextView) findViewById(R.id.btn_contents)).setTextColor(UiUtil.getColorList(this, R.color.white, mConfig.getThemeColor()));
            ((TextView) findViewById(R.id.btn_highlights)).setTextColor(UiUtil.getColorList(this, R.color.white, mConfig.getThemeColor()));
            findViewById(R.id.btn_contents).setBackgroundDrawable(UiUtil.convertColorIntoStateDrawable(this, mConfig.getThemeColor(), R.color.white));
            findViewById(R.id.btn_highlights).setBackgroundDrawable(UiUtil.convertColorIntoStateDrawable(this, mConfig.getThemeColor(), R.color.white));
        }


        loadContentFragment();
        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_contents).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadContentFragment();
            }
        });

        findViewById(R.id.btn_highlights).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHighlightsFragment();
            }
        });
    }

    private void loadContentFragment() {
        findViewById(R.id.btn_contents).setSelected(true);
        findViewById(R.id.btn_highlights).setSelected(false);
        TableOfContentFragment contentFrameLayout
                = TableOfContentFragment.newInstance(getIntent().getStringExtra(Constants.CHAPTER_SELECTED),
                getIntent().getStringExtra(Constants.BOOK_TITLE));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.parent, contentFrameLayout);
        ft.commit();
    }

    private void loadHighlightsFragment() {
        findViewById(R.id.btn_contents).setSelected(false);
        findViewById(R.id.btn_highlights).setSelected(true);
        String bookId = getIntent().getStringExtra(FolioReader.INTENT_BOOK_ID);
        String bookTitle= getIntent().getStringExtra(Constants.BOOK_TITLE);
        HighlightFragment highlightFragment = HighlightFragment.newInstance(bookId, bookTitle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.parent, highlightFragment);
        ft.commit();
    }

}
