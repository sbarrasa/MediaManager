package com.blink.s3api.model;


import javax.persistence.*;


@Entity
@Table(name = "FILE_META")
public class FileMeta {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "FILE_NAME")
    private String fileName;


    @Column(name = "CRC32")
    private long crc32;

    public FileMeta(String fileName, long crc32) {
        this.fileName = fileName;
        this.crc32 = crc32;
    }

	public FileMeta() {

	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getCrc32() {
		return crc32;
	}

	public void setCrc32(long crc32) {
		this.crc32 = crc32;
	}


}