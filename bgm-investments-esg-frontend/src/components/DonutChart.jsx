import {Cell, Pie, PieChart, ResponsiveContainer, Tooltip} from 'recharts';

export default function DonutChart({data, valueFormatter, titleFormatter}) {
    const formatValue =
        valueFormatter || ((v) => `${v} unità`); // default: mostra "unità"
    const formatTitle =
        titleFormatter || ((payload) => `Asset ${payload?.label ?? ''}`);

    return (
        <ResponsiveContainer width="100%" height={300}>
            <PieChart>
                <Pie
                    data={data}
                    dataKey="value"
                    nameKey="label"
                    innerRadius={60}
                    outerRadius={100}
                    label={({label}) => label}
                >
                    {data.map((entry, i) => (
                        <Cell key={i} fill={entry.color}/>
                    ))}
                </Pie>
                <Tooltip
                    contentStyle={{background: '#FFFF', border: 'none'}}
                    formatter={(v, n, p) => [
                        formatValue(v, p?.payload),
                        formatTitle(p?.payload),
                    ]}
                />
            </PieChart>
        </ResponsiveContainer>
    );
}