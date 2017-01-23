package com.yugai.danmu.message.handler;

import com.yugai.danmu.message.Message;
import com.yugai.danmu.socket.Connector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by yugai on 2016-4-7.
 */
public abstract class CastMessageHandler<T extends Message> extends TargetTypeMessageHandler {
    private Constructor<T> mConstructor;

    public CastMessageHandler(String type, Class<T> clz) {
        super(type);
        try {
            mConstructor = clz.getConstructor(Message.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleMessage(Connector connector, Message msg) {
        T t;
        try {
            t = mConstructor.newInstance(msg);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        handleMessage0(connector, t);
    }

    protected abstract void handleMessage0(Connector connector, T t);
}
