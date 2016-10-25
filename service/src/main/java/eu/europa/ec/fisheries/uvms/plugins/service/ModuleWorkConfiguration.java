/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.plugins.service;

import eu.europa.ec.fisheries.uvms.plugins.constants.UploaderConstants;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by kovian on 14/10/2016.
 */
public class ModuleWorkConfiguration {

    private String moduleName;
    private Map<String, String> moduleDirectories;
    private Set<String> supportedFiles;

    @PostConstruct
    private void init(){
        moduleName = StringUtils.EMPTY;
        moduleDirectories = new HashMap<>();
        supportedFiles = new HashSet<>();
    }

    public ModuleWorkConfiguration(String moduleName, Map<String, String> moduleDirectories, Set<String> supportedFiles) {
        this.moduleName = moduleName;
        this.moduleDirectories = moduleDirectories;
        this.supportedFiles = supportedFiles;
    }

    public String getUploadDirectory(){
        return moduleDirectories.get(UploaderConstants.UPLOAD);
    }
    public String getProcessedDirectory(){
        return moduleDirectories.get(UploaderConstants.PROCESSED);
    }
    public String getRefusedDirectory(){
        return moduleDirectories.get(UploaderConstants.REFUSED);
    }
    public String getFailedDirectory() {
        return moduleDirectories.get(UploaderConstants.FAILED);
    }

    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
    public Map<String, String> getModuleDirectories() {
        return moduleDirectories;
    }
    public void setModuleDirectories(Map<String, String> moduleDirectories) {
        this.moduleDirectories = moduleDirectories;
    }
    public Set<String> getSupportedFiles() {
        return supportedFiles;
    }
    public void setSupportedFiles(Set<String> supportedFiles) {
        this.supportedFiles = supportedFiles;
    }


}
