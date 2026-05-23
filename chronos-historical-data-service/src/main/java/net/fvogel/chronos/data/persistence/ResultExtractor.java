package net.fvogel.chronos.data.persistence;

import org.neo4j.driver.Result;

public interface ResultExtractor<T> {

    T extract(Result result);

}
