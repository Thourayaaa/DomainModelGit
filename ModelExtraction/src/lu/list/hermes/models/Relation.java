package lu.list.hermes.models;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
@Entity
@Table(name = "Relation")

public class Relation {
	@Id
    @GeneratedValue
    @Column(name = "IDr")
	private long IDr;
   
    public long getIDr(){
        return IDr;
    }	
    @Column(name = "relation",length = 100000)

    private String relation;
        
    @Column(name = "relationNL", length = 100000)

	private String relationNL;

    @OneToMany(fetch = FetchType.EAGER,mappedBy="relation")
    private Set<EntityRel> SubjObj;

	public Set<EntityRel> getSubjObj() {
		return this.SubjObj;
	}

	public void setSubjObj(Set<EntityRel> entities) {
		this.SubjObj = entities;
	}
    
	   
	
	@ManyToOne
    @JoinColumn(name = "IdDoc")
    private  Document document;
	
	
		public void setDocument (Document d)
	{
		this.document = d;
	}
		
    public void setrelationNL(String rel)
    {
    	this.relationNL = rel;
    }
    
    public String getrelationNL()
    {
    	return relationNL;
    }
	
	
	public Document getDocument ()
	{
		return document;
	}
	
	
	
	public void setrelation(String rel)
	{
		this.relation = rel;
	}
	
	public String getrelation()
	{
		return relation;
	}
	
	
		
	
	
	
}
