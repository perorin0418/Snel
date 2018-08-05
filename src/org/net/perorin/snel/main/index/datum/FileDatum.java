package org.net.perorin.snel.main.index.datum;

public class FileDatum extends Datum {

	/** 最終更新日 */
	public long date;

	/** ファイルサイズ */
	public long size;

	@Override
	public String toString() {
		return "FileDatum path:" + path + ", name:" + name + ", date:" + date + ", size:" + size;
	}
}
