package pet.wifispots.data.model;

import lombok.Data;
import pet.wifispots.data.model.address.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public @Data class Spot {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String password;
	private String name;
	private String description;
	private String address;
	@ManyToOne
	@JoinColumn
	private Country country;
	@ManyToOne
	@JoinColumn
	private State state;
	@ManyToOne
	@JoinColumn
	private City city;
	@ManyToOne
	@JoinColumn
	private PostalCode postalCode;
	private String url;
	@ManyToOne
	@JoinColumn(name="user_fk")
	private User user;
	@ManyToOne
	@JoinColumn
	private Category category;
	@Temporal(TemporalType.DATE)
	private Date verified;
	@Temporal(TemporalType.DATE)
	private Date created = new Date();
	private boolean personal = false;
	@OneToMany
	@JoinTable
	private List<Comment> comments;
    @Embedded
	private Point point;
}
