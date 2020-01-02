package com.croshe.android.base.listener;

import java.util.List;

/**
 * Created by Janesen on 2017/6/25.
 */
public abstract class PageDataCallBack<T> {

	/**
	 * 加载数据
	 *
	 * @param data 数据列表
	 */
	public boolean loadData(List<T> data) {
		return loadData(data, -1, true);
	}


//    /**
//     * 加载数据
//     *
//     * @param data        数据列表
//     * @param appendIndex 追加数据的索引
//     */
//    public boolean loadData(List<T> data, boolean appendIndex) {
//        return loadData(data, appendIndex, false);
//    }

	/**
	 * 加载数据
	 *
	 * @param data       数据列表
	 * @param isLoadDone 是否已加载完毕
	 */
	public boolean loadData(List<T> data, boolean isLoadDone) {
		return loadData(data, -1, isLoadDone);
	}

	/**
	 * 加载数据
	 *
	 * @param data       数据列表
	 * @param isLoadDone 是否已加载完毕
	 */
	public boolean loadData(List<T> data, boolean isLoadDone, boolean isStopLoading) {
		return loadData(data, -1, isLoadDone, isStopLoading);
	}

	/**
	 * 加载数据
	 *
	 * @param data        数据列表
	 * @param appendIndex 追加数据的索引
	 * @param isLoadDone  是否已加载完毕
	 */
	public boolean loadData(List<T> data, int appendIndex, boolean isLoadDone) {
		return loadData(-1, data, appendIndex, isLoadDone, true);
	}

	/**
	 * 加载数据
	 *
	 * @param data        数据列表
	 * @param appendIndex 追加数据的索引
	 * @param isLoadDone  是否已加载完毕
	 */
	public boolean loadData(List<T> data, int appendIndex, boolean isLoadDone, boolean isStopLoading) {
		return loadData(-1, data, appendIndex, isLoadDone, isStopLoading);
	}


	/**
	 * 加载数据
	 *
	 * @param page 当前页
	 * @param data 数据列表
	 */
	public boolean loadData(int page, List<T> data) {
		return loadData(page, data, -1, false, true);
	}

	/**
	 * 加载数据
	 *
	 * @param page        当前页
	 * @param data        数据列表
	 * @param appendIndex 追加数据的索引
	 */
	public boolean loadData(int page, List<T> data, int appendIndex) {
		return loadData(page, data, -1, false, true);
	}


	/**
	 * 加载数据
	 *
	 * @param page       当前页
	 * @param data       数据列表
	 * @param isLoadDone 是否已加载完毕
	 */
	public boolean loadData(int page, List<T> data, boolean isLoadDone) {
		return loadData(page, data, -1, isLoadDone, true);
	}

	/**
	 * 加载数据
	 *
	 * @param page       当前页
	 * @param data       数据列表
	 * @param isLoadDone 是否已加载完毕
	 */
	public boolean loadData(int page, List<T> data, boolean isLoadDone, boolean isStopLoading) {
		return loadData(page, data, -1, isLoadDone, isStopLoading);
	}

	/**
	 * 加载数据
	 *
	 * @param page        当前页
	 * @param data        数据列表
	 * @param appendIndex 追加数据的索引
	 * @param isLoadDone  是否已加载完毕
	 */
	public abstract boolean loadData(int page, List<T> data,
									 int appendIndex,
									 boolean isLoadDone,
									 boolean isStopLoading);


	/**
	 * 清除数据
	 */
	public void clearData() {

	}


	/**
	 * 数据加载完毕
	 */
	public void loadDone() {

	}

	/**
	 * 追加数据
	 *
	 * @param data
	 */
	public boolean appendData(List<T> data) {
		return appendData(data, -1);
	}

	/**
	 * 追加数据
	 *
	 * @param data
	 * @param appendIndex
	 */
	public boolean appendData(List<T> data, int appendIndex) {

		return false;
	}

	/**
	 * 追加数据
	 *
	 * @param data
	 */
	public boolean appendData(T data) {
		return appendData(data, -1);
	}

	/**
	 * 追加数据
	 *
	 * @param data
	 * @param appendIndex
	 */
	public boolean appendData(T data, int appendIndex) {

		return false;
	}


	/**
	 * 取消加载
	 *
	 * @param page
	 */
	public void cancelLoad(int page) {

	}

	/**
	 * 取消加载
	 */
	public void cancelLoad() {
		cancelLoad(-1);
	}

}
