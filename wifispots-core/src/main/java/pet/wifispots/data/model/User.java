package pet.wifispots.data.model;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@ToString(exclude="spots")
public @Data class User {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(nullable = false, unique = true)
	@NotEmpty(message="Email can't be empty.")
	@Email(message="Email should have @email format.")
	private String email;
	private String password;
	private boolean blocked = false;
	@Enumerated(EnumType.STRING)
    @Column(name = "\"role\"")
    private UserRole role = UserRole.USER;
	@Temporal(TemporalType.TIMESTAMP)
	private Date since;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLogin;
	@OneToMany(cascade= CascadeType.PERSIST, mappedBy = "user")
	private List<Spot> spots = new ArrayList<>();
	
	public void addSpot(Spot spot) {
		spots.add(spot);
	}
	
	@PrePersist
	private void initSinceDate() {
		if(Objects.isNull(since))
			since = new Date();
	}
}
