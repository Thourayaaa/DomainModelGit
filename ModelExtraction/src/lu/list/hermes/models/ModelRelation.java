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
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ModelRelation")

public class ModelRelation {
	
	@Id
    @GeneratedValue
    @Column(name = "iDr")
    private long iDr;
  
    public long getiDr(){
        return iDr;
    }
    @Column(name = "RelationName", length = 100000)

	private String RelationName;
    
    @Column(name = "BaseForm", length = 100000)

	private String BaseForm;
    @Column(name = "Identifier", length = 100000)

	private String Identifier;
    @OneToMany(fetch = FetchType.EAGER,mappedBy="ModelRelation")
    private Set<Range> range;

	public Set<Range> getrange() {
		return this.range;
	}

	public void setrange(Set<Range> range) {
		this.range = range;
	}
    
	 @OneToMany(fetch = FetchType.EAGER,mappedBy="ModelRelation")
	    private Set<Domain> domain;

		public Set<Domain> getdomain() {
			return this.domain;
		}

		public void setdomain(Set<Domain> domain) {
			this.domain = domain;
		}
	
	
    
    public void setIdentifier (String r)
	{
		this.Identifier = r;
	}
	
	public String getIdentifier()
	{
		return Identifier;
	}
	
    
    
    public void setRelationName (String r)
	{
		this.RelationName = r;
	}
	
	public String getRelationName()
	{
		return RelationName;
	}
	
	
	
	
	public void setBaseform(String infinitive)
	{
		this.BaseForm = infinitive;
	}
	
	public String getBaseForm()
	{
		return BaseForm;
	}
	
	
	}
