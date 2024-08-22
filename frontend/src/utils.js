export function formatDate(date) {
    if (date?.length != 3) 
        return null 
    else {
        const year = date[2].toString().padStart(2, '0')
        const month = date[1].toString().padStart(2, '0')
        const day = date[0].toString().padStart(2, '0')

        return (year + "/" + month + "/" + day) 
    }
}

export function wait(duration) {
    return new Promise(resolve => setTimeout(resolve, duration))
}

export function getCurrentDate () {
    const today = new Date()
    const year = today.getFullYear()
    const month = String(today.getMonth() + 1).padStart(2, '0')
    const day = String(today.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
}

export function bytesToMb(bytes) {
    return bytes / (1024 * 1024) // 1 MB = 1024 * 1024 bytes
}