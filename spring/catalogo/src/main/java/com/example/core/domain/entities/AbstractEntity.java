package com.example.core.domain.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@MappedSuperclass
public abstract class AbstractEntity<E> {
	private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
	@Transient
	protected final Log log = LogFactory.getLog(getClass());
	
	@Transient
	@JsonIgnore
	@SuppressWarnings("unchecked")
	public Set<ConstraintViolation<E>> getErrors() {
		return validator.validate((E)this);
	}

	@JsonIgnore
	@Transient
	public Map<String, String> getErrorsFields() {
		Set<ConstraintViolation<E>> lst = getErrors();
		if (lst.isEmpty())
			return null;
		Map<String, String> errors = new HashMap<>();
		lst.stream().sorted((a,b)->(a.getPropertyPath().toString() + ":" + a.getMessage()).compareTo(b.getPropertyPath().toString() + ":" + b.getMessage()))
			.forEach(item -> errors.put(item.getPropertyPath().toString(), 
					(errors.containsKey(item.getPropertyPath().toString()) ? errors.get(item.getPropertyPath().toString()) + ", " : "") 
					+ item.getMessage()));
		return errors;
	}

	@JsonIgnore
	@Transient
	public String getErrorsMessage() {
		Set<ConstraintViolation<E>> lst = getErrors();
		if (lst.isEmpty())
			return "";
		StringBuilder sb = new StringBuilder("ERRORES:");
		getErrorsFields().forEach((fld, err) -> sb.append(" " + fld + ": " + err + "."));
		return sb.toString();
	}

	@Transient
	@JsonIgnore
	public boolean isValid() {
		return getErrors().size() == 0;
	}

	@Transient
	@JsonIgnore
	public boolean isInvalid() {
		return !isValid();
	}

	@PostLoad
	protected void postLoad() {
		log.debug("PostLoad: " + this.getClass().getSimpleName() + " " + this.toString());
	}

	@PrePersist
	protected void prePersist() {
		log.debug("PrePersist: " + this.getClass().getSimpleName() + " " + this.toString());
		if (isInvalid()) {
			throw new ConstraintViolationException("Entidad no válida", getErrors());
		}
	}

	@PreUpdate
	protected void preUpdate() {
		log.debug("PreUpdate: " + this.getClass().getSimpleName() + " " + this.toString());
		if (isInvalid()) {
			throw new ConstraintViolationException("Entidad no válida", getErrors());
		}
	}

	@PreRemove
	protected void preRemove() {
		log.debug("PreRemove: " + this.getClass().getSimpleName() + " " + this.toString());
	}

	@PostPersist
	protected void postPersist() {
		log.debug("PostPersist: " + this.getClass().getSimpleName() + " " + this.toString());
	}

	@PostUpdate
	protected void postUpdate() {
		log.debug("PostUpdate: " + this.getClass().getSimpleName() + " " + this.toString());
	}

	@PostRemove
	protected void postRemove() {
		log.debug("PostRemove: " + this.getClass().getSimpleName() + " " + this.toString());
	}
	
}