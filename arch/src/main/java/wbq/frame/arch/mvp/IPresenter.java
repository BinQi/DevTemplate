package wbq.frame.arch.mvp;

/**
 * Created by Jerry on 2019/4/9 4:22 PM
 */
public interface IPresenter {
    /**
     * call this method to initiate
     */
    void initiate();

    /**
     * release you resource in this method
     */
    void destory();
}
