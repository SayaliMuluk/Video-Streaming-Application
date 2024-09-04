package com.stream.app.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "yt_course")
public class Course {
	
	@Id
	private String id;
	private String title;
	

//	@OneToMany(mappedBy = "course")
//	private List<Video> list = new ArrayList<>();
}
