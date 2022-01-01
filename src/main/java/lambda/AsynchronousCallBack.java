package lambda;

interface OnGeekAsyncEventListener {

    // this can be any type of method
    void onGeekEvent();
}

class BClass implements OnGeekAsyncEventListener {

    private OnGeekAsyncEventListener mListener; // listener field

    // setting the listener
    public void registerOnGeekEventListener(OnGeekAsyncEventListener mListener)
    {
        this.mListener = mListener;
    }

    // My Asynchronous task
    public void doGeekStuff()
    {
        // An Async task always executes in new thread
        new Thread(new Runnable() {
            public void run()
            {
                // perform any operation
                System.out.println("Performing operation in Asynchronous Task");
                // check if listener is registered.
                if (mListener != null) {
                    // invoke the callback method of class A
                    mListener.onGeekEvent();
                }
            }
        }).start();
    }

    @Override
    public void onGeekEvent()
    {
        System.out.println("Performing callback after Asynchronous Task");
        // perform some routine operation
    }
    // some class A methods
}

public class AsynchronousCallBack {

    public static void main(String[] args) {
        BClass obj = new BClass();
        OnGeekAsyncEventListener mListener = new BClass();
        obj.registerOnGeekEventListener(mListener);
        obj.doGeekStuff();
    }
}
