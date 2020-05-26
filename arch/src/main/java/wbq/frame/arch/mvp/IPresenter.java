package wbq.frame.arch.mvp;

/**
 * Created by Jerry on 2019/4/9 4:22 PM
 */
public interface IPresenter {
    /**
     * initiate
     */
    void initiate();

    /**
     * call this method to release you resource in this method
     */
    void destroy();
}
