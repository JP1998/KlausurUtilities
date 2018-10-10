/*
 *     Copyright 2017 Jean-Pierre Hotz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.jeanpierrehotz.utils.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * This class provides you the possibility to retrieve all the files inside a directory.<br/>
 * You can define whether or not to search in sub directories.<br/>
 * You can also filter the files by file-ending
 */
public class FileLister {

    /**
     * private construor since this class works in static context only
     */
    private FileLister(){}

    /**
     * This method gives you every file in the folder whose path has been given, whereas every file
     * with an file-ending that is not contained in fileEndings will be filtered. If fileEndings is not
     * given every file will be listed. Any subdirectory will be ignored.
     *
     * @param dir the directory to list files from
     * @param fileEndings the fileendings of the files to list; null or empty if all files are to be listed
     * @return the list of files in the folder defined by dir with one of the file-endings in fileEndings
     */
    public static List<File> listFiles(String dir, String... fileEndings) {
        return listFiles(dir, false, fileEndings);
    }

    /**
     * This method gives you every file in the folder whose path has been given in {@code dir}, whereas every file
     * with an file-ending that is not contained in {@code fileEndings} will be filtered. If {@code fileEndings} is not
     * given every file will be listed. Subdirectories will be listed, in case {@code searchSubDir} is {@code true}.
     *
     * @param dir the directory to list files from
     * @param searchSubDir whether to list files from subdirectories or not
     * @param fileEndings the fileendings of the files to list; null or empty if all files are to be listed
     * @return the list of files in the folder defined by dir with one of the file-endings in fileEndings,
     *         whereas subdirectories are considered according to {@code searchSubDir}
     */
    public static List<File> listFiles(String dir, boolean searchSubDir, String... fileEndings) {
        fileEndings = toLowerCase(fileEndings);

        Stack<File> directories = new Stack<>();
        directories.push(new File(dir));

        List<File> files = new ArrayList<>();

//      as long as there is a directory to list from
        while (!directories.empty()) {
//          we get the current folder
            File currDir = directories.pop();

//          list all the files inside the directory
            File[] currentFiles = currDir.listFiles();

//          if there are any files in the directory
            if(currentFiles != null) {
//              we'll check every file
                for (File f : currentFiles) {
//                  if it is a directory
                    if (f.isDirectory()) {
//                      and we have to search in subdirectories
                        if (searchSubDir) {
//                          we'll add the file to the directories we'll search in
                            directories.push(f);
                        }
                    }
//                  otherwise; if it is a file an is to be listed by the file-ending
                    else if (isToList(f, fileEndings)) {
//                      we'll add the file to the listed files
                        files.add(f);
                    }
                }
            }
        }

        return files;
    }

    /**
     * This method changes all the given Strings into their lowercase counterparts, and gives them all
     * in a String array back.
     *
     * @param toChange the Strings to convert to lowercase
     * @return the lowercase strings
     */
    private static String[] toLowerCase(String... toChange){
//      return null if the given arguments are null
        if(toChange == null){
            return null;
        }

//      convert every item to lowercase an store it in an array
        String[] changed = new String[toChange.length];
        for(int i = 0; i < changed.length; i++){
            changed[i] = toChange[i].toLowerCase();
        }

//      and return all the changed items
        return changed;
    }

    /**
     * This method determines whether the given file has one of the endings listed in fileEndings, and thus
     * whether it is to be listed in terms of {@link #listFiles(String, boolean, String...)} and {@link #listFiles(String, String...)}
     *
     * @param f the file to check for file-endings
     * @param fileEndings the file-endings to approve
     * @return whether or not the given file has one of the given file-endings
     */
    private static boolean isToList(File f, String... fileEndings){
//      we list every file, if there is no file-ending given
        if(fileEndings == null || fileEndings.length == 0){
            return true;
        }

//      we determine the file-ending of the file
        String fileEnding = determineLowercaseFileEndng(f);

//      if there is at least one file-ending we'll go through every ending
        for(String end : fileEndings) {
//          and if the file-ending is equal to the file-ending to allow
            if(fileEnding.equals(end)) {
//              we'll return true
                return true;
            }
        }

//      if none of the file-endings are equal we'll return false
        return false;
    }

    /**
     * This method determines the lowercase file-ending of the given file
     *
     * @param f the file whose file-ending is to be determined
     * @return the lowercase file-ending of the file
     */
    private static String determineLowercaseFileEndng(File f){
        String absPath = f.getAbsolutePath();
//      determine the last index of the char '.' (thus the beginning of the file-ending)
        int fileEndingIndex = absPath.lastIndexOf('.');

//      if there is an occurrence of the char '.'
        if(fileEndingIndex != -1){
//          we'll return the string from that index (excluding '.' => + 1) converted to lowercase
            return absPath.substring(fileEndingIndex + 1).toLowerCase();
        } else {
//          if there is no occurrence of the char '.' we'll return an empty string since the file has no file-ending
            return  "";
        }
    }
}
