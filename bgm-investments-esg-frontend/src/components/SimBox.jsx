export default function SimBox({title, value, desc, color}) {
    return (
        <div
            style={{
                border: `1px solid ${color}`,
                borderRadius: 10,
                padding: 12,
                background: 'rgba(255,255,255,0.04)',
            }}
            title={desc}
        >
            <h4 style={{margin: 0, color}}>{title}</h4>
            <p style={{fontSize: 26, margin: '4px 0', color}}>{value}</p>
            <p style={{opacity: 0.7, margin: 0}}>{desc}</p>
        </div>
    );
}