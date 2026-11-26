package com.emberdepths.strategy;

import java.io.Serializable;
import java.util.Random;

public interface AttackStrategy extends Serializable {

    String getName();

    String getDescription();

    int calculateDamage(int attack, Random random);

    int adjustDefense(int defense);
}
