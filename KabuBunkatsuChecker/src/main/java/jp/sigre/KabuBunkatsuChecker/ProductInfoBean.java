/**
 *
 */
package jp.sigre.KabuBunkatsuChecker;


/**
 * @author sigre
 * 作品情報を格納するBean
 */
public class ProductInfoBean {

	private int    id = -1;
	private String title;
	private String author;
	private String url;
	private int    releaseDate = 18000101;
	private int    flag; //表示要否etc

	public ProductInfoBean(int id, String title, String author, String url, int releaseDate, int flag) {
		setId(id);
		setTitle(title);
		setAuthor(author);
		setUrl(url);
		setReleaseDate(releaseDate);
		setFlag(flag);
	}

	public ProductInfoBean() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(int releaseDate) {
		this.releaseDate = releaseDate;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String[] getInfoAsAry() {
		String id     = String.valueOf(getId() );
		String title  = (getTitle()		 != null) ? getTitle()	: "";
		String author = (getAuthor()	 != null) ? getAuthor()	: "";
		String url    = (getUrl()		 != null) ? getUrl()	: "";
		String date   = String.valueOf(getReleaseDate());
		String flag   = String.valueOf(getFlag());
		String[] result = {id, title, author, url, date, flag};
		return result;
	}

	@Override
	public String toString() {
		return "ProductInfoBean [id=" + id + ", title=" + title + ", author="
				+ author + ", url=" + url + ", releaseDate=" + releaseDate
				+ ", flag=" + flag + "]";
	}


}
