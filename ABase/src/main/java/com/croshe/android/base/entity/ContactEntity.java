package com.croshe.android.base.entity;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Janesen on 2017/2/17.
 */

public class ContactEntity {

    private int contactId;
    private String contactName;
    private String allLetter;
    private String allFirstLetter;
    private String sortKey;
    private String sortKeyBySys;
    private String contactPhone;
    private String contactTimestamp;

    private String contactLetters;
    private String contactFirstLetters;




    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getAllLetter() {
        if(StringUtils.isEmpty(allLetter)){
            return contactLetters;
        }
        return allLetter;
    }

    public void setAllLetter(String allLetter) {
        this.allLetter = allLetter;
    }

    public String getAllFirstLetter() {
        if(StringUtils.isEmpty(allFirstLetter)){
            return getContactFirstLetters();
        }
        return allFirstLetter;
    }

    public void setAllFirstLetter(String allFirstLetter) {
        this.allFirstLetter = allFirstLetter;
    }

    public String getSortKey() {
        if (StringUtils.isEmpty(sortKey)) {
            sortKey=getAllFirstLetter().substring(0, 1).toUpperCase();
        }
        return getSortKey(sortKey);
    }

    private String getSortKey(String sortKey) {
        if (sortKey.matches("[A-Z]")) {
            return sortKey;
        }
        return "#";
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public String getSortKeyBySys() {
        if (StringUtils.isEmpty(sortKeyBySys)) {
            return getSortKey();
        }
        return sortKeyBySys;
    }

    public void setSortKeyBySys(String sortKeyBySys) {
        this.sortKeyBySys = sortKeyBySys;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactTimestamp() {
        return contactTimestamp;
    }

    public void setContactTimestamp(String contactTimestamp) {
        this.contactTimestamp = contactTimestamp;
    }


    public String getContactLetters() {
        return contactLetters;
    }

    public void setContactLetters(String contactLetters) {
        this.contactLetters = contactLetters;
    }

    public String getContactFirstLetters() {
        if (StringUtils.isEmpty(contactFirstLetters)) {
            contactFirstLetters = "#";
        }
        return contactFirstLetters;
    }

    public void setContactFirstLetters(String contactFirstLetters) {
        this.contactFirstLetters = contactFirstLetters;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }
}
