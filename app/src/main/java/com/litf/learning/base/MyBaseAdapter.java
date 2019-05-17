package com.litf.learning.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * BaseAdapter
 */

public abstract class MyBaseAdapter<T> extends BaseAdapter {
    Context mContext;
    List<T> mList;

    public MyBaseAdapter() {
        this.mList = new ArrayList();
    }

    public MyBaseAdapter(Context context) {
        this.mContext = context;
        this.mList = new ArrayList();
    }

    public MyBaseAdapter(Context context, Collection<T> collection) {
        this(context);
        mList.addAll(collection);
    }

    protected <V extends View> V findViewById(View view, int id) {
        return (V) view.findViewById(id);
    }

    public int findPosition(T message) {
        int index = this.getCount();
        int position = -1;

        while (index-- > 0) {
            if (message.equals(this.getItem(index))) {
                position = index;
                break;
            }
        }

        return position;
    }

    public int findPosition(long id) {
        int index = this.getCount();
        int position = -1;

        while (index-- > 0) {
            if (this.getItemId(index) == id) {
                position = index;
                break;
            }
        }

        return position;
    }

    public void addCollection(Collection<T> collection) {
        this.mList.addAll(collection);
    }

    public void addCollection(T... collection) {
        if (collection == null)
            return;

        for (T item : collection) {
            mList.add(item);
        }
    }

    public void add(T t) {
        this.mList.add(t);
    }

    public void add(T t, int position) {
        this.mList.add(position, t);
    }

    public void remove(int position) {
        this.mList.remove(position);
    }

    public void removeAll() {
        this.mList.clear();
    }

    public void clear() {
        this.mList.clear();
    }

    public int getCount() {
        return this.mList == null ? 0 : this.mList.size();
    }

    public T getItem(int position) {
        return this.mList == null ? null : (position >= this.mList.size() ? null : this.mList.get(position));
    }

    public List<T> getList() {
        return this.mList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = this.newView(this.mContext, position, parent);
        }

        this.bindView(this.mContext, view, position, this.getItem(position));
        return view;
    }

    protected abstract View newView(Context context, int position, ViewGroup parentView);

    protected abstract void bindView(Context context, View view, int position, T model);
}
