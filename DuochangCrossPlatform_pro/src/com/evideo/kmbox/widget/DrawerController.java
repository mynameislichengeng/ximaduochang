package com.evideo.kmbox.widget;

import java.util.ArrayList;


import com.evideo.kmbox.model.observer.drawer.DrawerStateSubject;
import com.evideo.kmbox.widget.Drawer.OnCloseListener;
import com.evideo.kmbox.widget.Drawer.OnOpenListener;


/**
 * @brief : [主界面抽屉菜单控制类，跟Drawer控件搭配使用]
 * <p>目前，菜单最多有4级
 */
public class DrawerController {
    
    private ArrayList<Drawer> mDrawerList = new ArrayList<Drawer>();
    
    private static DrawerController instance;
    
    private DrawerController() {
    }
    
    public static DrawerController getInstance() {
        if(instance == null) {
            instance = new DrawerController();
        }
        return instance;
    }
    
    /**
     * @brief : [添加抽屉]
     * @param drawer
     */
    public void addDrawer(final Drawer drawer) {
        if(!mDrawerList.contains(drawer)) {
            mDrawerList.add(drawer);
            drawer.setOnCloseListener(new OnCloseListener() {
                @Override
                public void onClosed() {
                    if(drawer.isNeedNotifyClose()) {
                        drawer.setNeedNotifyClose(false);
                        notifyUpperDrawerClose(drawer);
                    } else if (drawer.isNeedOpenAfterClosed()) {
                        drawer.openAfterClosed();
                    }
                    if(mDrawerList.indexOf(drawer) == 0) {
                        // TODO
                    }
                }
            });
            drawer.setOnOpenListener(new OnOpenListener() {
                @Override
                public void onOpened() {
                    // TODO Auto-generated method stub
                    if(drawer.isNeedNotifyOpen()) {
                        drawer.setNeedNotifyOpen(false);
//                        notifyNextDrawerOpen();
                        notifyNextDrawerOpen(drawer);
                    }
                }
            });
        }
    }
    
    public void clearDrawer() {
        mDrawerList.clear();
    }
    
    /**
     * @brief : [打开指定的抽屉，如果上一级抽屉正在打开，等待上一级抽屉打开完毕后再打开自己]
     * @param drawer
     */
    public void openDrawer(Drawer drawer) {
        if(isListEmpty()) {
            return;
        }
        if(!mDrawerList.contains(drawer)) {
            return;
        }
        int index = mDrawerList.indexOf(drawer);
        if(index > 0) {
            if(!mDrawerList.get(index - 1).isOpened()) {    //如果上一级菜单没有完全打开
                mDrawerList.get(index - 1).setNeedNotifyOpen(true);
            } else {
                drawer.openDrawer();
            }
        } else if (index == 0) {
            drawer.openDrawer();
        }
    }
    
    private void notifyNextDrawerOpen(Drawer drawer) {
        if(isListEmpty() || !mDrawerList.contains(drawer)) {
            return;
        }
        int i = mDrawerList.indexOf(drawer) + 1;
        if (i < mDrawerList.size()) {
            openDrawer(mDrawerList.get(i));
        }
    }
    
    /**
     * @brief : [关闭抽屉，按照从下级往上级的方式关闭抽屉]
     * @param drawer
     */
    public void closeDrawer(Drawer drawer) {
        if(isListEmpty()) {
            return;
        }
        if(!mDrawerList.contains(drawer)) {
            return;
        }
        int index = mDrawerList.indexOf(drawer);
        for(int i = mDrawerList.size() - 1; i >= index; i--) {
            if(!mDrawerList.get(i).isClosed()) {
                if(i > index) {
                    mDrawerList.get(i).setNeedNotifyClose(true);
                }
                mDrawerList.get(i).closeDrawer();
                break;
            }
        }
    }
    
    /**
     * @brief : [关闭抽屉]
     * @param level    关闭第几级 从1开始
     */
    public void closeDrawer(int level) {
        if(level > 0 && level <= mDrawerList.size()) {
            if(!isDrawerClosed(level)) {
                closeDrawer(mDrawerList.get(level - 1));
            }
        }
    }
    
    private void notifyUpperDrawerClose(Drawer drawer) {
        if(isListEmpty() || !mDrawerList.contains(drawer)) {
            return;
        }
        int i = mDrawerList.indexOf(drawer) - 1;
        if(i >= 0) {
            closeDrawer(mDrawerList.get(i));
        }
    }
    
    private boolean isListEmpty() {
        return mDrawerList == null || mDrawerList.size() <= 0;
    }
    
    /**
     * @brief : [判断是否所有抽屉都处于关闭状态]
     * @return
     */
    public boolean isAllDrawerClosed() {
        if(isListEmpty()) {
            return true;
        }
        for(Drawer drawer : mDrawerList) {
            if(!drawer.isClosed()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * @brief : [判断指定层级的抽屉是否处于完全关闭状态]
     * @param level 从1开始
     * @return
     */
    public boolean isDrawerClosed(int level) {
        if(level > 0 && level <= mDrawerList.size()) {
            return mDrawerList.get(level - 1).isClosed();
        }
        return true;
    }
    
    /**
     * @brief : [判断指定层级的抽屉是否处于完全打开状态]
     * @param level
     * @return
     */
    public boolean isDrawerOpened(int level) {
        if(level > 0 && level <= mDrawerList.size()) {
            return mDrawerList.get(level - 1).isOpened();
        }
        return false;
    }
    
    /**
     * @brief : [关闭最后一级的抽屉]
     */
    public void closeLastDrawer() {
        if(mDrawerList == null) {
            return;
        }
        int focusedLevel = getFocusedDrawerLevel();
        for(int i = mDrawerList.size() - 1; i >= 0; i--) {
            Drawer drawer = mDrawerList.get(i);
            if(drawer != null && !drawer.isClosed()) {
                if(focusedLevel == (i + 1) && focusedLevel != 1) { // 焦点正好在关闭的抽屉上,需要重置焦点到被关闭的前一级菜单上
                    DrawerStateSubject.getInstance().setNeedResetFocus(true);
                } else {
                    DrawerStateSubject.getInstance().setNeedResetFocus(false);
                }
                closeDrawer(drawer);
                break;
            }
        }
    }
    
    /**
     * @brief : [获取有焦点的菜单级数]
     * @return
     */
    public int getFocusedDrawerLevel() {
        for(int i = 0; i < mDrawerList.size(); i++) {
            if(mDrawerList.get(i).hasFocus()) {
                return i + 1;
            }
        }
        return -1;
    }
    
    /**
     * @brief : [判断该级菜单是否有焦点]
     * @param level
     * @return
     */
    public boolean hasFocuseInLevel(int level) {
        if(level > 0 && level <= mDrawerList.size()) {
            return mDrawerList.get(level - 1).hasFocus();
        } else {
            return false;
        }
    }
    
}
