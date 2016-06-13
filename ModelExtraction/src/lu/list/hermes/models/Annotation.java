package lu.list.hermes.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Annotation")

public class Annotation {

	@Id
    @GeneratedValue
    @Column(name = "idAnn")
    private long idAnn;
    public long getidAnn(){
        return idAnn;
    }
    @Column(name = "Annotation")

	private String Annotation;
    @Column(name = "db")

	private String db;
    @Column(name = "indexa")

	private int indexa;
    @Column(name = "uria")

	private String uria;
    @Column(name = "longa")

	private  int longa;
	@ManyToOne
    @JoinColumn(name = "IdDoc")
    private  Document document;
	
	
	public void setDocument (Document d)
	{
		this.document = d;
	}
	
	public Document getDocument ()
	{
		return document;
	}
	
	
	public void seturia (String uri)
	{
		this.uria = uri;
	}
	
	public String geturia()
	{
		return uria;
		
	}
	public void setAnnotation(String ann)
	{
		this.Annotation = ann;
	}
	
	public String getAnnotation()
	{
		return Annotation;
	}
	public void setdb(String dbpedia)
	{
		this.db = dbpedia;
	}
	
	
	public String getdb()
	{
		return db;
	}
	
	public void setindexa(int indx)
	{
		this.indexa = indx;
	}
	
	public int getindexa()
	{
		return indexa;
	}
	
	public void setlonga(int leng)
	{
		this.longa = leng;
	}
	
	public int getlonga()
	{
		return longa;
	}
	
	
	
	
	

}
