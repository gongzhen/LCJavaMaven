package lambda;

interface OnGeekEventListener {

    // this can be any type of method
    void onGeekEvent();
}

class A implements OnGeekEventListener {

    @Override
    public void onGeekEvent()
    {
        System.out.println("Performing callback after synchronous Task");
        // perform some routine operation
    }
    // some class A methods
}

    public class SynchronousCallBack {

    private OnGeekEventListener mListener; // listener field

    // setting the listener
    public void registerOnGeekEventListener(OnGeekEventListener mListener)
    {
        this.mListener = mListener;
    }

    // my synchronous task
    public void doGeekStuff()
    {

        // perform any operation
        System.out.println("Performing callback before synchronous Task");

        // check if listener is registered.
        if (this.mListener != null) {

            // invoke the callback method of class A
            mListener.onGeekEvent();
        }
    }

    public static void main(String[] args) {
        SynchronousCallBack obj = new SynchronousCallBack();
        OnGeekEventListener mListener = new A();
        obj.registerOnGeekEventListener(mListener);
        obj.doGeekStuff();
    }
}
