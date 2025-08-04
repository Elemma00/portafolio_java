import requests
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.ticker as mtick


API_URL = "http://localhost:8080/api/portafolio/1"
params = {
    "fecha_inicio": "2022-02-15",
    "fecha_fin": "2023-02-10"
}

response = requests.get(API_URL, params=params)
data = response.json()

if isinstance(data, dict):
    data = [data]

rows = []
activo_ids = set()
activo_nombres = {}

for entry in data:
    for activo in entry.get('activos', []):
        activo_ids.add(activo['id'])
        activo_nombres[activo['id']] = activo['nombre']

activo_ids = sorted(list(activo_ids))

for entry in data:
    if 'fecha' not in entry:
        print("Entrada sin 'fecha':", entry)
        continue
    row = {
        'fecha': entry['fecha'],
        'V_t': entry.get('valor_total', None)
    }

    # Le asocio los pesos a cada activo
    for activo_id in activo_ids:
        activo = next((a for a in entry.get('activos', []) if a['id'] == activo_id), None)
        row[f"w_{activo_id}"] = activo['weight'] if activo else 0
    rows.append(row)

if not rows:
    print("No se encontraron datos válidos en la respuesta de la API.")
    print("Respuesta recibida:", data)
    exit(1)

df = pd.DataFrame(rows)
df['fecha'] = pd.to_datetime(df['fecha'])

w_cols = [col for col in df.columns if col.startswith('w_')]
vt_col = 'V_t'

plt.figure(figsize=(14, 6))

# Se muestra el gráfico stacked area para w_{i,t} para todos los activos
plt.stackplot(
    df['fecha'],
    [df[col] for col in w_cols],
    labels=[f"{activo_nombres[int(col[2:])]}" for col in w_cols], 
    alpha=0.6)
plt.ylabel('w_{i,t} (%)')
plt.title('Evolución de pesos del portafolio 1 (w_{i,t})')
plt.legend(loc='center left', bbox_to_anchor=(1, 0.5))
plt.grid(axis='y', alpha=0.3)
plt.gca().yaxis.set_major_formatter(mtick.PercentFormatter(1.0))
plt.tight_layout()

# Gráfico de línea para V_t
plt.figure(figsize=(14, 4))
plt.plot(df['fecha'], 
         df[vt_col], 
         color='blue', 
         label='V_t', 
         linewidth=2)
plt.ylabel('V_t (USD)')
plt.xlabel('Fecha')
plt.title('Evolución de Valor total del portafolio 1 (V_t)')
plt.legend()
plt.gca().yaxis.set_major_formatter(mtick.StrMethodFormatter('${x:,.0f}'))

plt.show()