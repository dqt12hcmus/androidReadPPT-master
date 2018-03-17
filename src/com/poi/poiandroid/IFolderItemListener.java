package com.poi.poiandroid;

import java.io.File;

/**
 * Created by QuangThai on 6/9/2017.
 */
public interface IFolderItemListener {

    void OnCannotFileRead(File file);//implement what to do folder is Unreadable
    void OnFileClicked(File file);//What to do When a file is clicked
}