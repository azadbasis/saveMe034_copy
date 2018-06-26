package com.nanosoft.bd.saveme.activity;

import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.nanosoft.bd.saveme.R;
import com.nanosoft.bd.saveme.newsfeed.ListListener;
import com.nanosoft.bd.saveme.newsfeed.RssItem;
import com.nanosoft.bd.saveme.newsfeed.RssReader;

import java.util.List;

public class NewsFeedViewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    Toolbar toolbar;
    TabHost tabHost;
    ListView itcItems;
    NavigationView navigationView;

    private ProgressDialog mProgressDialog;

    NewsFeedViewActivity.GetRSSDataTask task;

    ImageView paperIconIv ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButtonAndDrawer();
        initialize(savedInstanceState);


    }



    private void animation(int layoutId){

        View view = (View) findViewById(layoutId);

        Animation animation = new TranslateAnimation(0,1500f, 0, 0);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(animation);
        animationSet.setDuration(800);
        animationSet.setFillAfter(true);
        view.startAnimation(animationSet);

    }

    private void initialize(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        // Then get the TabHost
        /*tabHost = (TabHost) findViewById(R.id.tabhost);
        LocalActivityManager mlam = new LocalActivityManager(this, false);
        mlam.dispatchCreate(savedInstanceState);
        tabHost.setup(mlam);
*/

        paperIconIv  = (ImageView)findViewById(R.id.paperIconIv);
        itcItems = (ListView) findViewById(R.id.rssChannelListView);

        task = new NewsFeedViewActivity.GetRSSDataTask();

        setPaper(R.drawable.prothom_alo,"Prothom Alo","http://www.prothom-alo.com/");


    }


    private class GetRSSDataTask extends AsyncTask<String, Void, List<RssItem>> {

        @Override
        protected void onPreExecute() {

            itcItems.setAdapter(null);

            mProgressDialog = new ProgressDialog(NewsFeedViewActivity.this);
            mProgressDialog.setMessage("Loading........");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected List<RssItem> doInBackground(String... urls) {
            try {
                if (isOnline()) {
                    // Create RSS reader
                    RssReader rssReader = new RssReader(urls[0]);

                    // Parse RSS, get items
                    return rssReader.getItems();
                }

            } catch (Exception e) {
                Log.e("RssChanelActivity", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<RssItem> result) {
            try {
                if (isOnline()) {

                    if (result!= null) {
                        ArrayAdapter<RssItem> adapter = new ArrayAdapter<RssItem>(NewsFeedViewActivity.this, android.R.layout.simple_list_item_1, result);
                        itcItems.setAdapter(adapter);
                        itcItems.setOnItemClickListener(new ListListener(result, NewsFeedViewActivity.this));

                        mProgressDialog.dismiss();
                    }else {
                        itcItems.setAdapter(null);
                        Toast.makeText(NewsFeedViewActivity.this, "News Server Down", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }

                }

            } catch (Exception e) {
                Log.e("RssChanelActivity", e.getMessage(), e);
            }
        }

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }


    }

    private void FloatingActionButtonAndDrawer() {

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).getSubMenu().getItem(0).setChecked(true);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            animation(R.id.drawer_layout);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    finish();

                    // super.onBackPressed();
                }
            }, 700);

           // super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about) {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            return true;
        }
        if (id == R.id.action_home) {
            final Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.action_help) {
            String url = "http://saveme.com.bd/#guide";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        }
        if (id == R.id.action_payment) {
            startActivity(new Intent(getApplicationContext(), AppPurchaseActivity.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_prothom_alo) {

            setPaper(R.drawable.prothom_alo,"Prothom Alo","http://www.prothom-alo.com/");

        } else if (id == R.id.nav_jugantor) {
            // navigationView.getMenu().getItem(0).getSubMenu().getItem(0).setChecked(false);
            setPaper(R.drawable.jugantor_icon,"Jugantor","http://www.jugantor.com/rss_online.xml");

        } else if (id == R.id.nav_samakal) {

            setPaper(R.drawable.samakal_icon,"Samakal","http://bangla.samakal.net/rss.xml");

        } else if (id == R.id.nav_bhorerkagoj) {

            setPaper(R.drawable.bhorerkagoj_logo,"Bhorerkagoj","http://www.bhorerkagoj.net/online/");

        } else if (id == R.id.nav_amardesh) {

            setPaper(R.drawable.amardesh_icon,"Amar Desh","http://amardesh24.com/bangla/rss.xml?feed=true");

        } else if (id == R.id.nav_daily_sanggram) {

            setPaper(R.drawable.sangram_icon,"Daily Sangram","http://feeds.feedburner.com/DailySangram");

        }else if (id == R.id.nav_bd_news24) {

            setPaper(R.drawable.bdnews24_icon,"Bd News24","http://goo.gl/YEHCrk");


        } else if (id == R.id.nav_bangla_news24) {

            setPaper(R.drawable.bangla_news24_icon,"Bangla News24","http://www.banglanews24.com/rss/rss.xml");

        } else if (id == R.id.nav_bbc_bangla) {

            setPaper(R.drawable.bbc_bangla_icon,"BBC Bangla","http://feeds.bbci.co.uk/bengali/rss.xml");

        } else if (id == R.id.nav_al_jazeera) {

            setPaper(R.drawable.al_jazeera_icon2,"Al Jazeera","http://www.aljazeera.com/xml/rss/all.xml");


        } else if (id == R.id.nav_sydney_herald) {

            setPaper(R.drawable.sydney_hearld_icon,"Sydney Herald","http://www.smh.com.au/rssheadlines/top.xml");

        } else if (id == R.id.nav_washington_post) {

            setPaper(R.drawable.washington_post_icon,"Washington Post","http://feeds.washingtonpost.com/rss/world");
        }else if (id == R.id.nav_espncricinfo_live) {

            setPaper(R.drawable.espncricinfologo,"Espn Cricket Scores","http://static.cricinfo.com/rss/livescores.xml");
        }else if (id == R.id.nav_espncricinfo) {

            setPaper(R.drawable.espncricinfologo,"Espncricinfo","http://www.espncricinfo.com/rss/content/story/feeds/0.xml");
        }else if (id == R.id.nav_wall_street_journal) {
            setPaper(R.drawable.wall_street_journal_icon,"The Wall Street Journal","http://www.wsj.com/xml/rss/3_7085.xml");
        }else if (id == R.id.nav_new_york_times) {
            setPaper(R.drawable.new_york_times_icon,"The New York Times","http://rss.nytimes.com/services/xml/rss/nyt/World.xml");
        }else if (id == R.id.nav_the_guardian) {
            setPaper(R.drawable.the_guardian_icon,"The Guardian","https://www.theguardian.com/world/rss");
        }else if (id == R.id.nav_bbc_news) {
            setPaper(R.drawable.bbc_news_icon,"BBC News","http://feeds.bbci.co.uk/news/world/rss.xml?edition=uk");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setPaper(int imageId,String title, String url){

        if (title.equals("Prothom Alo")){

        }else {
            navigationView.getMenu().getItem(0).getSubMenu().getItem(0).setChecked(false);
        }

        setTitle(title);
        paperIconIv.setImageResource(imageId);
        task = new NewsFeedViewActivity.GetRSSDataTask();
        task.execute(url);

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalActivityManager mlam = new LocalActivityManager(this, true);
        mlam.dispatchResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalActivityManager mlam = new LocalActivityManager(this, false);
        mlam.dispatchPause(isFinishing());
    }


}
