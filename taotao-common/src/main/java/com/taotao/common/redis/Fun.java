package com.taotao.common.redis;

public interface Fun<T, E> {

	public T callback(E e);

}
