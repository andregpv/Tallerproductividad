Descripción

El sistema de gestión de rentas para Navidad Express MTY tiene como objetivo digitalizar y automatizar el proceso completo de administración de trajes navideños. El proyecto reemplaza el uso de hojas de Excel y procesos manuales, facilitando el registro de clientes, control de pagos, seguimiento de trajes y generación de reportes.


Problema Identificado

Actualmente, la empresa realiza todas sus operaciones manualmente: registro de clientes, pagos, fechas y disponibilidad de trajes. Este enfoque genera:
-Errores frecuentes al capturar datos.
-Pérdida de información.
-Lentitud para atender a los clientes.
-Dificultad para validar disponibilidad de trajes.
-Falta de control sobre pagos y estatus de renta.


Solución Propuesta

Desarrollar una aplicación ligera que permita:
-Registrar clientes, pagos y trajes de forma rápida.
-Consultar disponibilidad en tiempo real.
-Actualizar estatus de cada renta (separado, entregado, regresado).
-Generar reportes para rentas activas, completadas e ingresos.
-Automatizar cálculos de saldos y validaciones de fechas.


Arquitectura General

La solución propuesta se compone de:
-Frontend: interfaz ligera (Swing / JavaFX o web simple según avance).
-Backend: lógica en Java.
-Base de datos: archivo local o embebido (SQLite / JSON / CSV según implementación).
-Servicios: módulos separados para rentas, pagos, reportes y usuarios.
-CI/CD: GitHub Actions para pruebas automáticas.
