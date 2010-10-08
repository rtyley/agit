/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/roberto/development/connectbot/src/com/madgag/android/ssh/AuthAgent.aidl
 */
package com.madgag.android.ssh;
public interface AuthAgent extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.madgag.android.ssh.AuthAgent
{
private static final java.lang.String DESCRIPTOR = "com.madgag.android.ssh.AuthAgent";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.madgag.android.ssh.AuthAgent interface,
 * generating a proxy if needed.
 */
public static com.madgag.android.ssh.AuthAgent asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.madgag.android.ssh.AuthAgent))) {
return ((com.madgag.android.ssh.AuthAgent)iin);
}
return new com.madgag.android.ssh.AuthAgent.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_sendIdentities:
{
data.enforceInterface(DESCRIPTOR);
java.util.Map _result = this.sendIdentities();
reply.writeNoException();
reply.writeMap(_result);
return true;
}
case TRANSACTION_sign:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
byte[] _arg1;
_arg1 = data.createByteArray();
byte[] _result = this.sign(_arg0, _arg1);
reply.writeNoException();
reply.writeByteArray(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.madgag.android.ssh.AuthAgent
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public java.util.Map sendIdentities() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.Map _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_sendIdentities, _data, _reply, 0);
_reply.readException();
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_result = _reply.readHashMap(cl);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public byte[] sign(byte[] publicKey, byte[] data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(publicKey);
_data.writeByteArray(data);
mRemote.transact(Stub.TRANSACTION_sign, _data, _reply, 0);
_reply.readException();
_result = _reply.createByteArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_sendIdentities = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_sign = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public java.util.Map sendIdentities() throws android.os.RemoteException;
public byte[] sign(byte[] publicKey, byte[] data) throws android.os.RemoteException;
}
