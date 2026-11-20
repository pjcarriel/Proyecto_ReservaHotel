package com.example.backend_reservashotel.infrastructure.mappers;

import com.example.backend_reservashotel.domain.models.DetalleReserva;
import com.example.backend_reservashotel.domain.models.Reserva;
import com.example.backend_reservashotel.infrastructure.entities.DetalleReservaEntity;
import com.example.backend_reservashotel.infrastructure.entities.HabitacionEntity;
import com.example.backend_reservashotel.infrastructure.entities.HuespedEntity;
import com.example.backend_reservashotel.infrastructure.entities.ReservaEntity;

import java.util.List;
import java.util.function.Function;

public final class ReservaMapper {

    private ReservaMapper() {
    }

    public static Reserva toDomain(ReservaEntity ety) {
        List<DetalleReserva> detalles = ety.getDetalles().stream()
                .map(dte -> new DetalleReserva(
                        dte.getId(),
                        HabitacionMapper.toDomain(dte.getHabitacion()),
                        dte.getNoches(),
                        dte.getPrecioNoche(),
                        dte.getSubtotal()
                ))
                .toList();

        return new Reserva(
                ety.getId(),
                ety.getNumero(),
                HuespedMapper.toDomain(ety.getHuesped()),
                ety.getFechaEntrada(),
                ety.getFechaSalida(),
                ety.getEstado(),
                detalles,
                ety.getSubtotal(),
                ety.getIva(),
                ety.getTotal(),
                ety.getCreado(),
                ety.getModificado()
        );
    }

    public static ReservaEntity toEntity(Reserva reservaDomain, HuespedEntity huespedEty,
                                         Function<Long, HabitacionEntity> buscarHabitacion) {
        ReservaEntity ety = new ReservaEntity();
        ety.setNumero(reservaDomain.getNumero());
        ety.setHuesped(huespedEty);
        ety.setFechaEntrada(reservaDomain.getFechaEntrada());
        ety.setFechaSalida(reservaDomain.getFechaSalida());
        ety.setEstado(reservaDomain.getEstado());
        ety.setSubtotal(reservaDomain.getSubtotal());
        ety.setIva(reservaDomain.getIva());
        ety.setTotal(reservaDomain.getTotal());

        reservaDomain.getDetalles().forEach(detalleDomain -> {
            DetalleReservaEntity detalleEty = new DetalleReservaEntity();
            toEntity(detalleDomain, detalleEty, ety,
                    buscarHabitacion.apply(detalleDomain.getHabitacion().getId()));
            ety.getDetalles().add(detalleEty);
        });

        return ety;
    }

    private static void toEntity(DetalleReserva detalleDomain, DetalleReservaEntity ety,
                                 ReservaEntity reservaEty, HabitacionEntity habitacionEty) {
        ety.setReserva(reservaEty);
        ety.setHabitacion(habitacionEty);
        ety.setNoches(detalleDomain.getNoches());
        ety.setPrecioNoche(detalleDomain.getPrecioNoche());
        ety.setSubtotal(detalleDomain.getSubtotal());
    }
}