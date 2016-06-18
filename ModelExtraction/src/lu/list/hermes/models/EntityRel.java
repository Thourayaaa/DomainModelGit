package lu.list.hermes.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "EntityRel")

public class EntityRel {
	
	@Id
    @GeneratedValue
    @Column(name = "iDe")
    private long iDe;
  
    public long getiDe(){
        return iDe;
    }
    @Column(name = "Entitytext", length = 100000)

	private String Entitytext;
    @Column(name = "indexe")

	private int indexe;
    @Column(name = "longent")

	private int longent;
    @Column(name = "urient", length = 100000)

	private String urient;
    @Column(name = "label")

   	private String label;
	@ManyToOne
    @JoinColumn(name = "IDr")
    private  Relation relation;

	
	public void setRelation (Relation r)
	{
		this.relation = r;
	}
	
	public Relation getRelation()
	{
		return relation;
	}
	
	
	public void setEntitytext(String text)
	{
		this.Entitytext = text;
	}
	
	public String getEntitytext()
	{
		return Entitytext;
	}
	
	
	public void setindexe(int idx )
	{
		this.indexe = idx;
	}
	
	public int getindexe()
	{
		return indexe;
	}
	
	public void setlongent (int lengs)
	{
		this.longent = lengs;
	}
	
	public int getlongent()
	{
		return longent;
	}
	
	
	public void seturient(String uri)
	{
		this.urient = uri;
	}

	
	public String geturient()
	{
		return urient;
	}
	
	public void setlabel(String lab)
	{
		this.label = lab;
	}
	public String getlabel()
	{
		return label;
	}
	
	
	
}
