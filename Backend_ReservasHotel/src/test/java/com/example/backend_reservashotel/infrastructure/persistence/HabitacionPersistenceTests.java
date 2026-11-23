package com.example.backend_reservashotel.infrastructure.persistence;

import com.example.backend_reservashotel.application.ports.in.HabitacionIn;
import com.example.backend_reservashotel.application.ports.in.TipoHabitacionIn;
import com.example.backend_reservashotel.domain.models.EstadoHabitacion;
import com.example.backend_reservashotel.domain.models.Habitacion;
import com.example.backend_reservashotel.domain.models.TipoHabitacion;
import com.example.backend_reservashotel.infrastructure.repositories.TipoHabitacionRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class HabitacionPersistenceTests {
    @Autowired
    private HabitacionIn habitaciones;
    @Autowired
    private TipoHabitacionIn tipos;
    @Autowired
    private TipoHabitacionRepository tipoRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    void guardaElTipoElPrecioYLaAuditoriaYLosRecuperaDesdeLaBase() {
        TipoHabitacion tipo = crearTipo();
        Habitacion creada = habitaciones.crear(datos(), tipo.getId());
        entityManager.clear();

        Habitacion consultada = habitaciones.buscarPorId(creada.getId());

        assertEquals(tipo.getId(), consultada.getTipoHabitacion().getId());
        assertEquals(tipo.getNombre(), consultada.getTipoHabitacion().getNombre());
        assertEquals(new BigDecimal("45.50"), consultada.getPrecioNoche());
        assertEquals(0, consultada.getPiso());
        assertEquals(creada.getCreado(), consultada.getCreado());
        assertNotNull(consultada.getModificado());
    }

    @Test
    void laClaveForaneaImpideBorrarUnTipoAunqueSeOmitaElServicio() {
        TipoHabitacion tipo = crearTipo();
        habitaciones.crear(datos(), tipo.getId());
        entityManager.clear();

        assertThrows(DataIntegrityViolationException.class, () -> {
            tipoRepository.deleteById(tipo.getId());
            tipoRepository.flush();
        });
    }

    private TipoHabitacion crearTipo() {
        return tipos.crear(TipoHabitacion.nuevo("Tipo JPA " + UUID.randomUUID(),
                "Descripcion de tipo para prueba de persistencia", 2));
    }

    private Habitacion datos() {
        return new Habitacion(null, "JPA-" + UUID.randomUUID().toString().substring(0, 8),
                0, new BigDecimal("45.50"), EstadoHabitacion.DISPONIBLE, null, null, null);
    }
}
