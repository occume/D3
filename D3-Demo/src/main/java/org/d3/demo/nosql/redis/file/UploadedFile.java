package org.d3.demo.nosql.redis.file;

public class UploadedFile {
	private int contentLength;
	private String eTag, lastModified, contentType;
	private byte[] body = null;

	public int getContentLength() {
		return contentLength;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	public String getETag() {
		return eTag;
	}

	public void setETag(String eTag) {
		this.eTag = eTag;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public boolean exists() {
		return eTag != null;
	}

	public boolean isModified(String eTag_, String lastModified_) {
		return eTag_ == null ? !lastModified.equals(lastModified_) : !eTag.equals(eTag_);
	}

	public void validate() {
		if (body != null && body.length != contentLength) {
			body = null;
		}
	}
}