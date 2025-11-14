export default function Spinner({size = 18}) {
    return (
        <div
            className="spin"
            style={{
                width: size,
                height: size,
                border: '3px solid #0002',
                borderTopColor: 'var(--bgm-gold)',
                borderRadius: '50%',
                animation: 'spin 1s linear infinite',
            }}
        />
    );
}