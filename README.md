# AutonomousCar - SUA_6

Grupo: Isaac Marsden, Miguel Licea Cespedes, Yuliia Solomakha, Mario Akio Scappini Udagawa, Ángela Alonso Pérez

## Requisitos implementados

Servicios:
- ADS_L3-2
- ADS_L3-3
- ADS_L3-4
- ADS_L3-5
- ADS_L3-6

ADS:
- ADS-1

Interacción:
- INTERACT-2

## Divergencias del diseño

Varias decisiones tomadas durante la fase de diseño resultaron ser inadecuadas para la implementación, por lo que existen varias divergencias entre la implementación y el diseño:

### Configuración inicial

En el diseño, empezamos con una configuración de:

- L0_ManualDriving
- Engine
- Steering
- NotificationService
  - Speakers_AuditorySound

Esta configuración hemos reemplazado por una con el L3_HighwayChauffer activo para facilitar pruebas:

- L3_HighwayChauffer
  - Engine
  - Steering
  - RoadSensor
  - FrontDistanceSensor
  - LeftDistanceSensor
  - RightDistanceSensor
  - RearDistanceSensor
  - HumanSensors
  - NotificationService
    - Speakers.AuditoryBeep
    - SteeringWheel (interaction)
  - ParkInTheRoadShoulderFallbackPlan

### Propiedades del Knowledge

Quitadas:
 - nivel-autonomía
 - sensores-funcionamiento
 - plan-emergencia-actual
 - lidar-activo

Añadidas:
  - funcion-conduccion-anterior
  - manos-en-volante
  - asiento-conductor-ocupado
  - vibracion-volante
  - vibracion-asiento-conductor
  - errores-sensores-distancia-actual
  - errores-sensores-distancia-anterior

### Sondas

Quitadas:
  - sonda-sensor-fallo
  - sonda-fallo-sistémico

Añadidas:
  - sonda-asiento-conductor
  - sonda-manos-en-volante
  - sonda-asiento-conductor
  - sonda-error-sensor-distancia

### Monitores

Quitadas:
  - monitor-sensor-fallo
  - monitor-fallo-sistémico

Añadidas:
  - monitor-asiento-conductor
  - monitor-manos-en-volante
  - monitor-error-sensor-distancia

### Reglas

Quitadas:
- regla-transición-nivel-3-2
- regla-transición-nivel-3-1
- regla-fallo-sensor-distancia-nivel3
- regla-fallback-plan-park-shoulder-activo
- regla-fallback-plan-emergency-activo
- regla-prioritiza-sensor-distancia-nivel-3
- regla-prioritiza-sensor-distancia-nivel-2
- regla-prioritiza-sensor-distancia-nivel-1
- regla-fallo-sistémico

Añadidas:
 - regla-driver-seat-haptic-vibration
 - regla-steering-wheel-haptic-vibration
 - regla-error-distance-sensor

## Comandos

Añadimos dos nuevos comandos al simulador para facilitar pruebas:

1. `error [front | left | right | rear]` - Simula o arreglar un error en el sensor de distancia especificado.
2. `notification` - Simula una notificación del sistema para probar la interacción con el usuario.

## Pruebas para los requisitos implementados

---
```text
ADS_L3-2 — Highway ➜ Traffic Jam

Descripción
Con L3_HighwayChauffeur activo en autopista fluida, el tráfico pasa a atasco/colapso.

initialize
road type highway
road status fluid
show                 # HighwayChauffeur activo
road status jam      # (o collapsed)
show

Criterios de aceptación
	•	L3_HighwayChauffeur → STOPPED / INACTIVE
	•	L3_TrafficJamChauffeur → ACTIVE

⸻

ADS_L3-3 — Highway ➜ City

Descripción
Con L3_HighwayChauffeur activo en autopista fluida, el vehículo entra en ciudad.

initialize
road type highway
road status fluid
show                 # HighwayChauffeur activo
road type city
show

Criterios de aceptación
	•	L3_HighwayChauffeur → STOPPED / INACTIVE
	•	L3_CityChauffeur     → ACTIVE

⸻

ADS_L3-4 — Traffic Jam ➜ Highway

Descripción
Con L3_TrafficJamChauffeur activo en autopista congestionada, la vía se vuelve fluida.

initialize
road type highway
road status jam
show                 # TrafficJamChauffeur activo
road status fluid
show

Criterios de aceptación
	•	L3_TrafficJamChauffeur → STOPPED / INACTIVE
	•	L3_HighwayChauffeur    → ACTIVE

⸻

ADS_L3-5 — Traffic Jam ➜ City

Descripción
Con L3_TrafficJamChauffeur activo en autopista congestionada, el vehículo entra en ciudad.

initialize
road type highway
road status jam
show                 # TrafficJamChauffeur activo
road type city
show

Criterios de aceptación
	•	L3_TrafficJamChauffeur → STOPPED / INACTIVE
	•	L3_CityChauffeur       → ACTIVE

⸻

ADS_L3-6 — City ➜ Highway / Traffic Jam

Descripción
Estando activa la función L3_CityChauffer circulando por ciudad, entramos
a autovía

Variante A — Autovía fluida

initialize
road type city
road status fluid
show                 # CityChauffeur activo
road status fluid    # (si no lo estaba)
road type highway
show

Criterios de aceptación
	•	L3_CityChauffeur    → STOPPED / INACTIVE
	•	L3_HighwayChauffeur → ACTIVE

Variante B — Autovía congestionada

initialize
road type city
road status fluid
show                 # CityChauffeur activo
road status jam      # (o collapsed)
road type highway
show

Criterios de aceptación
	•	L3_CityChauffeur        → STOPPED / INACTIVE
	•	L3_TrafficJamChauffeur  → ACTIVE

⸻

ADS-1

Descripción
Cuando un servicio de conducción autónoma (L3) o asistida (L2 ó L1) esté
activo, siempre debe utilizar los sensores disponibles más preferentes

initialize
show                 # DISTANCES - Front Active
error front
show                 # Distances - Front Inactive -> LIDAR Front Active

Criterios de aceptación
	• DISTANCES → INACTIVE
	• LIDAR → ACTIVE

⸻

INTERACT-2

Descripción
Estando activa cualquier función de conducción de nivel L3, y cambia la
situación de 'manos en el volante' del conductor

initialize
notification        # Speakers.AuditoryBeep & SteeringWheel
driver hands off-wheel
notification        # Speakers.AuditoryBeep & DriverSeat.HapticVibration
seat driver false
notification        # Speakers.AuditoryBeep

```