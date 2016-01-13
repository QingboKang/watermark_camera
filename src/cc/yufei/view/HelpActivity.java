package cc.yufei.view;


import java.util.ArrayList;  


import android.app.Activity;  
import android.os.Bundle;  
import android.os.Parcelable;  
import android.support.v4.view.PagerAdapter;  
import android.support.v4.view.ViewPager;  
import android.support.v4.view.ViewPager.OnPageChangeListener;  
import android.util.Log;
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.view.ViewGroup.LayoutParams;  
import android.view.Window;  
import android.widget.ImageView;  

public class HelpActivity extends Activity {

	ViewPager viewPager;  
    ArrayList<View> list;  
    ViewGroup main, group;  
    ImageView imageView;  
    ImageView[] imageViews;  
  
	private String LOG_TAG = "samsung_info";
	
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
    	Log.i(LOG_TAG, "before onCreate");
        super.onCreate(savedInstanceState);  
    	Log.i(LOG_TAG, "After onCreate");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
    	Log.i(LOG_TAG, "After requestWindowFeature");
        LayoutInflater inflater = getLayoutInflater();  
    	Log.i(LOG_TAG, "After getLayoutInflater");
        list = new ArrayList<View>();  
        list.add(inflater.inflate(R.layout.helpguide_item01, null));  
        list.add(inflater.inflate(R.layout.helpguide_item02, null));  
        list.add(inflater.inflate(R.layout.helpguide_item03, null));  
        list.add(inflater.inflate(R.layout.helpguide_item04, null));  
        list.add(inflater.inflate(R.layout.helpguide_item05, null));  
        list.add(inflater.inflate(R.layout.helpguide_item06, null));  
        list.add(inflater.inflate(R.layout.helpguide_item07, null));  
        list.add(inflater.inflate(R.layout.helpguide_item09, null));  
        list.add(inflater.inflate(R.layout.helpguide_item10, null));  
  
        imageViews = new ImageView[list.size()];  
        ViewGroup main = (ViewGroup) inflater.inflate(R.layout.activity_help, null);  
        // group是R.layou.main中的负责包裹小圆点的LinearLayout.  
        ViewGroup group = (ViewGroup) main.findViewById(R.id.viewGroup);  
  
        viewPager = (ViewPager) main.findViewById(R.id.viewPager);  
  
        for (int i = 0; i < list.size(); i++) {  
            imageView = new ImageView(HelpActivity.this);  
            imageView.setLayoutParams(new LayoutParams(10,10));  
            imageView.setPadding(10, 0, 10, 0);  
            imageViews[i] = imageView;  
            if (i == 0) {  
                // 默认进入程序后第一张图片被选中;  
               imageViews[i].setBackgroundResource(R.drawable.guide_01);  
            } else {  
                imageViews[i].setBackgroundResource(R.drawable.guide_10);  
            }  
            group.addView(imageView);  
        }  
  
        setContentView(main);  
  
        viewPager.setAdapter(new MyAdapter());  
        viewPager.setOnPageChangeListener(new MyListener());  
    }  
  
    class MyAdapter extends PagerAdapter {  
  
        @Override  
        public int getCount() {  
            return list.size();  
        }  
  
        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            return arg0 == arg1;  
        }  
  
        @Override  
        public int getItemPosition(Object object) {  
            // TODO Auto-generated method stub  
            return super.getItemPosition(object);  
        }  
  
        @Override  
        public void destroyItem(View arg0, int arg1, Object arg2) {  
            // TODO Auto-generated method stub  
            ((ViewPager) arg0).removeView(list.get(arg1));  
        }  
  
        @Override  
        public Object instantiateItem(View arg0, int arg1) {  
            // TODO Auto-generated method stub  
            ((ViewPager) arg0).addView(list.get(arg1));  
            return list.get(arg1);  
        }  
  
        @Override  
        public void restoreState(Parcelable arg0, ClassLoader arg1) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public Parcelable saveState() {  
            // TODO Auto-generated method stub  
            return null;  
        }  
  
        @Override  
        public void startUpdate(View arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void finishUpdate(View arg0) {  
            // TODO Auto-generated method stub  
  
        }  
    }  
  
    class MyListener implements OnPageChangeListener {  
  
        @Override  
        public void onPageScrollStateChanged(int arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void onPageScrolled(int arg0, float arg1, int arg2) {  
            // TODO Auto-generated method stub  
  
        }  
  
        @Override  
        public void onPageSelected(int arg0) {  
            for (int i = 0; i < imageViews.length; i++)
            {  
                imageViews[arg0]  
                        .setBackgroundResource(R.drawable.guide_01);  
                if (arg0 != i)
                {  
                    imageViews[i]  
                            .setBackgroundResource(R.drawable.guide_10);  
                }  
            }  
  
        }  
  
    }  
}
