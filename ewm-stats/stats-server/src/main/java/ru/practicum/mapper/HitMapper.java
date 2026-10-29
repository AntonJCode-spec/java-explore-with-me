package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.EndpointHit;
import ru.practicum.storage.model.Hit;

@UtilityClass
public class HitMapper {

    public static Hit mapToHit(EndpointHit endpointHit) {
        Hit hit = new Hit();
        hit.setApp(endpointHit.getApp());
        hit.setUri(endpointHit.getUri());
        hit.setIp(endpointHit.getIp());
        hit.setTimestamp(endpointHit.getTimestamp());

        return hit;
    }

    public static EndpointHit mapToEndpointHit(Hit hit, Long hitId) {
        EndpointHit endpointHit = new EndpointHit();

        endpointHit.setId(hitId);
        endpointHit.setApp(hit.getApp());
        endpointHit.setUri(hit.getUri());
        endpointHit.setIp(hit.getIp());
        endpointHit.setTimestamp(hit.getTimestamp());

        return endpointHit;
    }
}
