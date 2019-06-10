package com.litf.learning.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class MyBaseAdapter<E> extends BaseAdapter {

	protected List<E> data;
	protected Context context;

	public MyBaseAdapter(List<E> data, Context context) {
		super();
		this.data = data;
		this.context = context;
	}

	@Override
	public int getCount() {
		if (data == null)
			return 0;
		else
			return data.size();
	}

	@Override
	public E getItem(int position) {
		if (data == null || data.size() == 0)
			return null;
		else
			return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public List<E> getData() {
		return data;
	}

	public void setData(List<E> data) {
		this.data = data;
	}

	@Override
	public abstract View getView(int position, View convertView,
                                 ViewGroup parent);

	/*@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		try {
			if (data == null || data.size() <= 0) {
				((BaseActivity) context).onListViewDataEmpty();
			} else {
				((BaseActivity) context).onListViewDataNotEmpty();
			}
		} catch (Exception e) {
		}
	}*/
}
