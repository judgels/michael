package org.iatoki.judgels.michael.application.version;

import org.iatoki.judgels.play.model.AbstractModel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "michael_application_version")
public class ApplicationVersionModel extends AbstractModel {

    @Id
    @GeneratedValue
    public long id;

    public String applicationJid;

    public String name;
}
