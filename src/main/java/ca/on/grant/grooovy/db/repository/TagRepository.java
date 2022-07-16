package ca.on.grant.grooovy.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.on.grant.grooovy.db.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long>{
	List<Tag> findAll();
	Tag findById(long id);
}
