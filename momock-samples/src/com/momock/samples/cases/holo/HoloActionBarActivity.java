package com.momock.samples.cases.holo;

import android.os.Bundle;
import android.view.Menu;

import com.momock.app.CaseActivity;
import com.momock.samples.Cases;
import com.momock.samples.R;

public class HoloActionBarActivity extends CaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holo_action_bar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_holo_action_bar, menu);
        return true;
    }

	@Override
	protected String getCaseName() {
		return Cases.HOLO;
	}
}
