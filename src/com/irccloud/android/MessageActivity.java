package com.irccloud.android;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MessageActivity extends UserListActivity {
	int cid;
	long bid;
	String name;
	String type;
	TextView messageTxt;
	Button sendBtn;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        messageTxt = (TextView)findViewById(R.id.messageTxt);
        messageTxt.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
         	   if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
         		   new SendTask().execute((Void)null);
         	   }
         	   return true;
           	}
        });
        sendBtn = (Button)findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new SendTask().execute((Void)null);
			}
        });
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        if(savedInstanceState != null && savedInstanceState.containsKey("cid")) {
        	cid = savedInstanceState.getInt("cid");
        	bid = savedInstanceState.getLong("bid");
        	name = savedInstanceState.getString("name");
        	type = savedInstanceState.getString("type");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
    	super.onSaveInstanceState(state);
    	state.putInt("cid", cid);
    	state.putLong("bid", bid);
    	state.putString("name", name);
    	state.putString("type", type);
    }
    
    private class SendTask extends AsyncTask<Void, Void, Void> {
    	@Override
    	protected void onPreExecute() {
    		sendBtn.setEnabled(false);
    	}
    	
		@Override
		protected Void doInBackground(Void... arg0) {
			NetworkConnection conn = NetworkConnection.getInstance();
			if(conn.getState() == NetworkConnection.STATE_CONNECTED) {
				conn.say(cid, name, messageTxt.getText().toString());
			}
			return null;
		}
    	
		@Override
		protected void onPostExecute(Void result) {
			messageTxt.setText("");
    		sendBtn.setEnabled(true);
		}
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	if(getIntent() != null && getIntent().hasExtra("cid")) {
	    	cid = getIntent().getIntExtra("cid", 0);
	    	bid = getIntent().getLongExtra("bid", 0);
	    	name = getIntent().getStringExtra("name");
	    	type = getIntent().getStringExtra("type");
    	}
    	getSupportActionBar().setTitle(name);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	if(type.equalsIgnoreCase("channel"))
    		getSupportMenuInflater().inflate(R.menu.activity_message_channel, menu);
    	else if(type.equalsIgnoreCase("conversation"))
    		getSupportMenuInflater().inflate(R.menu.activity_message_conversation, menu);
    	else if(type.equalsIgnoreCase("console"))
    		getSupportMenuInflater().inflate(R.menu.activity_message_console, menu);

        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed
                // in the Action Bar.
                Intent parentActivityIntent = new Intent(this, MainActivity.class);
                parentActivityIntent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(parentActivityIntent);
                finish();
                return true;
            case R.id.menu_userlist:
            	Intent i = new Intent(this, UserListActivity.class);
            	i.putExtra("cid", cid);
            	i.putExtra("bid", bid);
            	i.putExtra("name", name);
            	startActivity(i);
            	break;
        }
        return super.onOptionsItemSelected(item);
    }
}