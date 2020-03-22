package com.evideo.kmbox.model.kmproxy;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;

import com.evideo.kmbox.util.TypeConvert;

public class XMLMessage  implements Parcelable
{
    private int mClientID   = -1;
    private byte[] messagebyte = new byte[sDefaultBufferSize];
    
    protected static final ByteOrder BYTEORDER = ByteOrder.LITTLE_ENDIAN;
    public static final int sDefaultBufferSize = 4096;
        

    public XMLMessage() {
    }
    
    public XMLMessage(XMLMessage xml){
        if(xml != null){
            this.setMessage(xml.getMessage());
            this.setClientID(xml.getClientID());
        }
    }

    public XMLMessage(Parcel pl) {
        pl.readByteArray(messagebyte);
        mClientID = pl.readInt();
        this.setMessage(TypeConvert.getByteBuffer(TypeConvert.getString(messagebyte)));
    }
    
    public static final Parcelable.Creator<XMLMessage> CREATOR = new Parcelable.Creator<XMLMessage>() 
    {  

        @Override  
         public XMLMessage createFromParcel(Parcel source) {  
                 return new XMLMessage(source); 
        }  

        @Override  
         public XMLMessage[] newArray( int size) {  
                 return new XMLMessage[size]; 
        }  
    };
    
    public ByteBuffer getMessage() {
        return ByteBuffer.wrap(messagebyte).order(BYTEORDER);
    }

    public void setMessage(ByteBuffer message) {
        ByteBuffer bu = message;
        byte[] temp = new byte[bu.remaining()];
        bu.get(temp, 0, bu.remaining());
        if (temp.length <= messagebyte.length) {
            Arrays.fill(messagebyte, (byte)0);
            System.arraycopy(temp, 0, messagebyte, 0, temp.length);
        }
    }

    public int getClientID() {
        return mClientID;
    }

    public void setClientID(int clientID) {
        mClientID = clientID;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(messagebyte); 
        dest.writeInt(mClientID);
        dest.writeValue(null);
    }
    
    public boolean isValid(){
        return true;
    }
    
}
