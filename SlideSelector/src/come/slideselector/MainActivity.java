package come.slideselector;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private GridView mGridView;
	private List<String> mList = new ArrayList<String>();
	private Context mContext;
	private AnimatedSelector animatedSelector;
	private View selector;
	
	private SlideAdapter mSlideAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGridView = (GridView)findViewById(R.id.my_gridview);
		selector = (View)findViewById(R.id.selector);
		mContext = this;
		mSlideAdapter = new SlideAdapter();
		for(int i =0;i<60;i++)
		{
			mList.add("item"+i);
		}
		mGridView.setAdapter(mSlideAdapter);
		setSelector(mGridView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void setSelector(GridView gridView) {
		animatedSelector = new AnimatedSelector(mContext,selector,
				gridView.getSelector());
		animatedSelector.hideView();
		gridView.setSelector(animatedSelector);
	}
	
	class SlideAdapter extends BaseAdapter
	{
		private TextView mTextView;
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(mList==null)
				return 0;
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			if(mList==null)
				return null;
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView==null)
			{
				convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, null);
			}
			mTextView = (TextView)convertView.findViewById(R.id.text);
			mTextView.setText(mList.get(position));
			return convertView;
		}
		
	}

}
