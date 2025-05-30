## Plan de pruebas nivel L3

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
road type highway
road status fluid    # (si no lo estaba)
show

Criterios de aceptación
	•	L3_CityChauffeur    → STOPPED / INACTIVE
	•	L3_HighwayChauffeur → ACTIVE

Variante B — Autovía congestionada

initialize
road type city
road status fluid
show                 # CityChauffeur activo
road type highway
road status jam      # (o collapsed)
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