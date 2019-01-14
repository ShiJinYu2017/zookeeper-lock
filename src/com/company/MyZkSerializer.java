package com.company;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.io.UnsupportedEncodingException;

public class MyZkSerializer implements ZkSerializer {
    String charset = "UTF-8";

    @Override
    public byte[] serialize(Object o) throws ZkMarshallingError {
        byte[] out = new byte[0];
        try {
            out= String.valueOf(o).getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return out;
    }
    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        String out = new String();
        try {
            out = new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return out;
    }
}
