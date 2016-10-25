/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.plugins.service.bean;

import eu.europa.ec.fisheries.uvms.exchange.model.util.DateUtils;
import eu.europa.ec.fisheries.uvms.plugins.constants.UploaderConstants;
import eu.europa.ec.fisheries.uvms.plugins.exception.ResponseMappingException;
import eu.europa.ec.fisheries.uvms.plugins.service.FileUploadListener;
import eu.europa.ec.fisheries.uvms.plugins.service.ModuleWorkConfiguration;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.jms.JMSException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by kovian on 13/10/2016.
 */
@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@DependsOn(value = {"UploadExchangeServiceBean", "WorkFlowsLoaderBean"})
public class FileUploadListenerBean implements FileUploadListener {

    @EJB
    private UploadExchangeServiceBean exchangeMessageProcucer;

    @EJB
    private WorkFlowsLoaderBean workLoader;

    private static final Logger LOG   = LoggerFactory.getLogger(FileUploadListenerBean.class);

    /**
     * Sets up the scheduler and checks for the existence of the 'needed for work' directories.
     * If any of the directories has not been configured then creates it.
     */
    @PostConstruct
    private void init(){
        Set<ModuleWorkConfiguration> works = workLoader.getWorks();
        LOG.info("\n\n--- THE UPLOADER MODULE WILL BE LISTENING TO SCHEDULED WORKS UNDER THE MAIN DIR : "+workLoader.getMainDir()+"\n");
        if(CollectionUtils.isNotEmpty(works)){
            checkWorkingDirectoriesExistence(works);
            logWorks(works);
        } else {
            LOG.warn("No folder has been scheduled for listening for any module in Uploader. Check 'config.properties' file in Uploader module.");
            return;
        }
        controlDirectoriesForNewFiles();
    }

    private void logWorks(Set<ModuleWorkConfiguration> works) {
        LOG.info("\n\n There have been scheduled directory listeners for ::::::::::::: [[ "+works.size()+" ]] ::::::::: modules.\n");
        for(ModuleWorkConfiguration work : works){
            LOG.info("\n\n ********************************************************************************************************\n");
            LOG.info("Working directories for module : "+work.getModuleName());
            LOG.info("\n\n Directories : "+work.getModuleDirectories());
            LOG.info("\n\n Supported files : "+work.getSupportedFiles());
            LOG.info("\n\n ********************************************************************************************************\n\n");
        }
    }


    /**
     * Controls for files in the configured directories for each ModuleWork every scheduler_config seconds.
     * If some found :
     *              1. if file is supported by the module then puts them in the configured work flow.
     *              2. if file is not supported by the module it gets 'thrown' in the refused foledr.
     *
     * @return worksHaveBeen found
     */
    @Override
    public boolean controlDirectoriesForNewFiles() {
        Set<ModuleWorkConfiguration> works = workLoader.getWorks();
        if(CollectionUtils.isEmpty(works)){
            LOG.warn("Uploader did not find any scheduled for 'file drop listening' configurations! Canceling all previous scheduling for the uploader.");
            return false;
        }
        for(ModuleWorkConfiguration workConfig : works){
            Set<File> filesList = getCleanFilesList(workConfig);
            if(CollectionUtils.isNotEmpty(filesList)) {
                readAndSendFilestToDestination(filesList, workConfig);
            }
        }
        return true;
    }

    private void readAndSendFilestToDestination(Set<File> filesList, ModuleWorkConfiguration workConfig) {
        for(File actualFile : filesList){
            String fileAsStr = null;
            try {
                LOG.info("\n\n-->>>> Found new File in the upload Dir of : "+workConfig.getModuleName()+" Module.. With File name : "+actualFile.getAbsolutePath());
                fileAsStr = readFile(actualFile.getPath(), StandardCharsets.UTF_8);
                exchangeMessageProcucer.sendMessageToExchange(fileAsStr, workConfig.getModuleName());
                LOG.info("\n\n-->>>> Finished reading/sending to the work flow and moving the found file : "+actualFile.getAbsolutePath()+" Module..");
                renameAndMoveFile(actualFile, workConfig.getProcessedDirectory());
            } catch (IOException | ResponseMappingException | JMSException e) {
                LOG.error("Couldn't read/move or send file to Exchange module. See stackTrace for more details : ",e);
                LOG.error("\n\nThe file  : ["+actualFile.getAbsolutePath()+"] will be moved to configured Failed directory..\n\n");
                try {
                    LOG.debug("Trying to move file to Failed directory!");
                    renameAndMoveFile(actualFile, workConfig.getFailedDirectory());
                    LOG.info("File moved successfully to Failed directory!");
                } catch (IOException e1) {
                    LOG.error("Couldn't move file to FAILED directory : ",e1);
                }
            }


        }
    }


    /**
     * Checks if a directory exists. If it doesn't it creates it.
     *
     * @param directoriesPaths
     */
    private void checkWorkingDirectoriesExistence(Set<ModuleWorkConfiguration> directoriesPaths) {
        for(ModuleWorkConfiguration workConfig : directoriesPaths){
            for(Map.Entry<String, String> entry : workConfig.getModuleDirectories().entrySet()){
                createDirectoryIfDoesNotExist(entry.getValue());
            }
        }
    }


    /**
     * Checks if a directory exists. If it doesn't it creates it.
     *
     * @param dirPath
     */
    private void createDirectoryIfDoesNotExist(String dirPath) {
        File theDir    = new File(dirPath);
        boolean exists = false;
        try {
            exists = theDir.mkdirs();
        } catch(SecurityException sex){
            LOG.error("JAVA Security check does not permit access to create Directory : ",dirPath, sex);
        }
        if(exists){
            LOG.warn("Directory : " + dirPath + " didn't exist! Created it..");
        }

    }


    /**
     * Gets the files list from the FA_XML_DIRECTORY_PATH directory.
     * Cleans it from eventually not supported or empty files (and directories) moving them to "refused directory".
     *
     * @return xmlFilesList
     * @param workConfig
     */
    private Set<File> getCleanFilesList(ModuleWorkConfiguration workConfig){
        String uploadDir        = workConfig.getUploadDirectory();
        File folder             = new File(uploadDir);
        File[] listOfFiles      = folder.listFiles();
        if(listOfFiles == null){
            return new HashSet<>();
        }
        Set<String> moduleSupportedFiles = workConfig.getSupportedFiles();
        Set<File> filesList  = new HashSet<>();
        for (int i = 0; i < listOfFiles.length; i++) {
            String fileName = listOfFiles[i].getName();
            if (listOfFiles[i].isFile()) { // Found file
                LOG.info("Processing " + fileName);
                if(fileIsSupported(fileName, moduleSupportedFiles) && listOfFiles[i].length() > 0){
                    filesList.add(listOfFiles[i]);
                    continue;
                }
                LOG.warn("Found an Unsupported file ("+fileName+") found in FA_XML_DIRECTORY_PATH directory "+uploadDir);
                try {
                    renameAndMoveFile(listOfFiles[i], workConfig.getRefusedDirectory());
                    LOG.warn("Moved the Unsupported file ("+fileName+") found in "+uploadDir+" to refused directory ("+workConfig.getRefusedDirectory()+"..");
                } catch (IOException e) {
                   LOG.error("Couldn't move file to destination directory",e);
                }
            } else if (listOfFiles[i].isDirectory()) { // Found Directoty (not allowed)
                LOG.warn("WARNING : Directory ("+fileName+") was found in the FA_XML_DIRECTORY_PATH! It will be ignored!");
            }
        }
        return filesList;
    }

    /**
     * Moves a file to the given "newPath" and deletes the original one;
     *
     * @param filePointer
     * @param newPath
     */
    private void renameAndMoveFile(File filePointer, String newPath) throws IOException {
        // Rename the file so if it it already exists in the 'processed' directory won't throw FileAlreadyExistsException.
        String newFilePath = StringUtils.EMPTY;
        try {
            newFilePath = getNewNameForFile(filePointer.getAbsolutePath());
            Path source = Paths.get(filePointer.getAbsolutePath());
            Files.move(source, source.resolveSibling(newFilePath));
        } catch (SecurityException sex){
            LOG.error("Failed to rename file in uploader module during file move..",sex);
        }
        FileUtils.moveFileToDirectory(new File(newFilePath), new File(newPath), false);
    }


    /**
     *  Controls if a file is supported, by controling its extension.
     *  As of this moment only XML files are considered;
     *
     * @param fileName
     * @param supportedExtensions
     * @return true/false
     */
    private boolean fileIsSupported(String fileName, Set<String> supportedExtensions) {
        String extension = getFileExtension(fileName);
        for(String supportedExtension : supportedExtensions){
            if(StringUtils.equalsIgnoreCase(extension, supportedExtension)){
                return true;
            }
        }
        return false;
    }

    /**
     * Stripes the file extensionf from the file name.
     *
     * @param fileName
     * @return
     */
    private static String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf(UploaderConstants.DOT);
        String extension = StringUtils.EMPTY;
        if (i >= 0) {
            extension = fileName.substring(i+1);
        }
        return extension;
    }

    /**
     * Changes a fileName into a filename that contains the date.
     *
     * @param fileName
     * @return
     */
    private static String getNewNameForFile(String fileName) {
        String[] extension;
        StringBuilder newName;
        String fileNameExtens = DateUtils.nowUTC().toDate().toString()
                .replaceAll(StringUtils.SPACE, StringUtils.EMPTY)
                .replaceAll(UploaderConstants.COLON, StringUtils.EMPTY);
        if (fileName.lastIndexOf(UploaderConstants.DOT) >= 0) { // Files with extension
            extension = fileName.split(UploaderConstants.ESCAPED_DOT);
            newName   = new StringBuilder(extension[0]).append(UploaderConstants.MINUS).append(fileNameExtens).append(UploaderConstants.DOT).append(extension[1]);
        } else { // Files without extension
            newName = new StringBuilder(fileName).append(UploaderConstants.MINUS).append(UploaderConstants.DOT).append(fileNameExtens);
        }
        return newName.toString();
    }

    /**
     * Read a file and return its content as string.
     *
     * @param path
     * @param encoding
     * @return
     * @throws IOException
     */
    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }



}
